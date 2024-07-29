package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.PlatformProxy;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.autocrafting.CraftingPattern;
import com.refinedmods.refinedstorage.common.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.common.api.autocrafting.PatternProviderItem;
import com.refinedmods.refinedstorage.common.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.content.DataComponents;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.support.CraftingMatrix;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.util.PlatformUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslationKey;

public class PatternItem extends Item implements PatternProviderItem {
    private static final Component HELP = createTranslation("item", "pattern.help");
    private static final MutableComponent FUZZY_MODE = createTranslation("item", "pattern.fuzzy_mode")
        .withStyle(ChatFormatting.YELLOW);

    public PatternItem() {
        super(new Item.Properties());
    }

    @Override
    public String getDescriptionId(final ItemStack stack) {
        final PatternState state = stack.get(DataComponents.INSTANCE.getPatternState());
        if (state != null) {
            return createTranslationKey("misc", "pattern." + state.type().getSerializedName());
        }
        return super.getDescriptionId(stack);
    }

    @Override
    public void appendHoverText(final ItemStack stack,
                                final TooltipContext context,
                                final List<Component> lines,
                                final TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, lines, tooltipFlag);
        final PatternState state = stack.get(DataComponents.INSTANCE.getPatternState());
        if (state == null) {
            return;
        }
        final CraftingPatternState craftingState = stack.get(DataComponents.INSTANCE.getCraftingPatternState());
        if (craftingState != null && craftingState.fuzzyMode()) {
            lines.add(FUZZY_MODE);
        }
    }

    boolean hasMapping(final ItemStack stack) {
        return stack.has(DataComponents.INSTANCE.getPatternState());
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
        final PatternState state = stack.get(DataComponents.INSTANCE.getPatternState());
        if (state == null) {
            return Optional.of(new HelpTooltipComponent(HELP));
        }
        return switch (state.type()) {
            case CRAFTING -> {
                final CraftingPatternState craftingState = stack.get(DataComponents.INSTANCE.getCraftingPatternState());
                final Level level = PlatformUtil.getClientLevel();
                if (craftingState == null || level == null) {
                    yield Optional.empty();
                }
                yield RefinedStorageApi.INSTANCE.getPattern(stack, level)
                    .filter(CraftingPattern.class::isInstance)
                    .map(CraftingPattern.class::cast)
                    .map(craftingPattern -> new CraftingPatternTooltipComponent(
                        HELP,
                        craftingPattern,
                        craftingState.input().input().width(),
                        craftingState.input().input().height()
                    ));
            }
            default -> Optional.empty();
        };
    }

    @Nullable
    @Override
    public UUID getId(final ItemStack stack) {
        final PatternState state = stack.get(DataComponents.INSTANCE.getPatternState());
        if (state == null) {
            return null;
        }
        return state.id();
    }

    @Override
    public Optional<Pattern> getPattern(final ItemStack stack, final Level level) {
        final PatternState state = stack.get(DataComponents.INSTANCE.getPatternState());
        if (state == null) {
            return Optional.empty();
        }
        return switch (state.type()) {
            case CRAFTING -> getCraftingPattern(stack, level);
            default -> Optional.empty();
        };
    }

    private Optional<Pattern> getCraftingPattern(final ItemStack stack, final Level level) {
        final CraftingPatternState craftingState = stack.get(DataComponents.INSTANCE.getCraftingPatternState());
        if (craftingState == null) {
            return Optional.empty();
        }
        return getCraftingPattern(level, craftingState);
    }

    private Optional<Pattern> getCraftingPattern(final Level level, final CraftingPatternState state) {
        final CraftingMatrix craftingMatrix = getFilledCraftingMatrix(state);
        final CraftingInput.Positioned positionedCraftingInput = craftingMatrix.asPositionedCraftInput();
        final CraftingInput craftingInput = positionedCraftingInput.input();
        return level.getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, craftingInput, level)
            .map(RecipeHolder::value)
            .map(recipe -> toCraftingPattern(level, recipe, craftingInput, state));
    }

    private CraftingMatrix getFilledCraftingMatrix(final CraftingPatternState state) {
        final CraftingInput.Positioned positionedInput = state.input();
        final CraftingInput input = positionedInput.input();
        final CraftingMatrix craftingMatrix = new CraftingMatrix(null, input.width(), input.height());
        for (int i = 0; i < input.size(); ++i) {
            craftingMatrix.setItem(i, input.getItem(i));
        }
        return craftingMatrix;
    }

    private CraftingPattern toCraftingPattern(final Level level,
                                              final CraftingRecipe recipe,
                                              final CraftingInput craftingInput,
                                              final CraftingPatternState state) {
        final List<List<PlatformResourceKey>> inputs = getInputs(recipe, state);
        final ResourceAmount output = getOutput(level, recipe, craftingInput);
        final List<ResourceAmount> byproducts = getByproducts(recipe, craftingInput);
        return new CraftingPattern(inputs, output, byproducts);
    }

    private List<List<PlatformResourceKey>> getInputs(final CraftingRecipe recipe, final CraftingPatternState state) {
        final List<List<PlatformResourceKey>> inputs = new ArrayList<>();
        for (int i = 0; i < state.input().input().size(); ++i) {
            final ItemStack input = state.input().input().getItem(i);
            if (input.isEmpty()) {
                inputs.add(Collections.emptyList());
            } else if (state.fuzzyMode() && i < recipe.getIngredients().size()) {
                final ItemStack[] ingredients = recipe.getIngredients().get(i).getItems();
                inputs.add(Arrays.stream(ingredients)
                    .map(item -> (PlatformResourceKey) ItemResource.ofItemStack(item))
                    .toList());
            } else {
                inputs.add(List.of(ItemResource.ofItemStack(input)));
            }
        }
        return inputs;
    }

    private ResourceAmount getOutput(final Level level,
                                     final CraftingRecipe recipe,
                                     final CraftingInput craftingInput) {
        final ItemStack outputStack = recipe.assemble(craftingInput, level.registryAccess());
        return new ResourceAmount(ItemResource.ofItemStack(outputStack), outputStack.getCount());
    }

    private List<ResourceAmount> getByproducts(final CraftingRecipe recipe, final CraftingInput craftingInput) {
        return recipe.getRemainingItems(craftingInput)
            .stream()
            .filter(byproduct -> !byproduct.isEmpty())
            .map(byproduct -> new ResourceAmount(ItemResource.ofItemStack(byproduct), byproduct.getCount()))
            .toList();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player.isCrouching()) {
            return new InteractionResultHolder<>(
                InteractionResult.CONSUME,
                new ItemStack(Items.INSTANCE.getPattern(), stack.getCount())
            );
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    public record CraftingPatternTooltipComponent(Component helpText,
                                                  CraftingPattern craftingPattern,
                                                  int width,
                                                  int height)
        implements TooltipComponent {
    }
}
