package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage2.platform.common.Platform;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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

    public SecurityCardItem() {
        super(new Item.Properties().stacksTo(1));
    }

    SecurityCardModel getModel(final ItemStack stack) {
        return new SecurityCardModel(stack);
    }

    boolean isActive(final ItemStack stack) {
        return SecurityCardModel.isActive(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            use(hand, serverPlayer, stack);
        }
        return InteractionResultHolder.consume(stack);
    }

    private void use(final InteractionHand hand, final ServerPlayer player, final ItemStack stack) {
        if (player.isCrouching()) {
            bindOrClear(player, stack);
            return;
        }
        Platform.INSTANCE.getMenuOpener().openMenu(player, new SecurityCardExtendedMenuProvider(
            PlatformApi.INSTANCE.createInventorySlotReference(player, hand),
            getModel(stack)
        ));
    }

    private void bindOrClear(final ServerPlayer player, final ItemStack stack) {
        if (stack.hasTag()) {
            clear(player, stack);
        } else {
            bind(player, stack);
        }
    }

    private void clear(final ServerPlayer player, final ItemStack stack) {
        stack.setTag(null);
        player.sendSystemMessage(createTranslation("item", "security_card.cleared"));
    }

    private void bind(final ServerPlayer player, final ItemStack stack) {
        final SecurityCardModel model = getModel(stack);
        model.setBoundPlayer(player);
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
        final SecurityCardModel model = getModel(stack);

        final String boundPlayerName = model.getBoundPlayerName();
        if (boundPlayerName == null) {
            lines.add(createTranslation("item", "security_card.unbound").withStyle(ChatFormatting.GRAY));
            return;
        }

        lines.add(createTranslation(
            "item",
            "security_card.bound",
            Component.literal(boundPlayerName).withStyle(ChatFormatting.YELLOW)
        ).withStyle(ChatFormatting.GRAY));

        PlatformApi.INSTANCE.getPermissionRegistry().getAll().forEach(permission -> {
            final boolean allowed = model.isAllowed(permission);
            final boolean dirty = model.isDirty(permission);
            final Style style = Style.EMPTY
                .withColor(allowed ? ChatFormatting.GREEN : ChatFormatting.RED)
                .withItalic(dirty);
            final Component permissionTooltip = Component.literal(allowed ? "✓ " : "❌ ")
                .append(permission.getName())
                .append(dirty ? " (*)" : "")
                .withStyle(style);
            lines.add(permissionTooltip);
        });
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
        return Optional.of(new HelpTooltipComponent(isActive(stack) ? BOUND_HELP : UNBOUND_HELP));
    }
}
