package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage2.platform.common.Platform;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

public class SecurityCardItem extends Item {
    private static final Component UNBOUND_HELP = createTranslation("item", "security_card.unbound.help");
    private static final Component BOUND_HELP = createTranslation("item", "security_card.bound.help");

    private static final String TAG_BOUND_TO = "boundto";
    private static final String TAG_BOUND_TO_NAME = "boundtoname";

    public SecurityCardItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.isCrouching()) {
                bindOrUnbind(serverPlayer, stack);
            } else {
                Platform.INSTANCE.getMenuOpener().openMenu(serverPlayer, new SecurityCardExtendedMenuProvider(
                    PlatformApi.INSTANCE.createInventorySlotReference(player, hand)
                ));
            }
        }
        return InteractionResultHolder.consume(stack);
    }

    private void bindOrUnbind(final ServerPlayer player, final ItemStack stack) {
        if (stack.hasTag()) {
            unbind(player, stack);
        } else {
            bind(player, stack);
        }
    }

    private void unbind(final ServerPlayer player, final ItemStack stack) {
        stack.setTag(null);
        player.sendSystemMessage(createTranslation(
            "item",
            "security_card.unbound"
        ));
    }

    private void bind(final ServerPlayer player, final ItemStack stack) {
        stack.getOrCreateTag().putUUID(TAG_BOUND_TO, player.getGameProfile().getId());
        stack.getOrCreateTag().putString(TAG_BOUND_TO_NAME, player.getGameProfile().getName());
        player.sendSystemMessage(createTranslation(
            "item",
            "security_card.bound",
            Component.literal(player.getGameProfile().getName()).withStyle(ChatFormatting.YELLOW)
        ));
    }

    @Override
    public void appendHoverText(final ItemStack stack,
                                @Nullable final Level level,
                                final List<Component> lines,
                                final TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        if (stack.getTag() == null) {
            lines.add(createTranslation("item", "security_card.unbound").withStyle(ChatFormatting.GRAY));
            return;
        }
        final Component boundToName = Component.literal(stack.getTag().getString(TAG_BOUND_TO_NAME))
            .withStyle(ChatFormatting.YELLOW);
        lines.add(createTranslation(
            "item",
            "security_card.bound",
            boundToName
        ).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
        return Optional.of(new HelpTooltipComponent(isActive(stack) ? BOUND_HELP : UNBOUND_HELP));
    }

    boolean isActive(final ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(TAG_BOUND_TO);
    }
}
