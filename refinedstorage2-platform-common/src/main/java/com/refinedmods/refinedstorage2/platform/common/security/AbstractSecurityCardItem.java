package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.Platform;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

abstract class AbstractSecurityCardItem<M extends SecurityCardModel> extends Item {
    protected AbstractSecurityCardItem(final Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(final ItemStack stack,
                                @Nullable final Level level,
                                final List<Component> lines,
                                final TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        final M model = createModel(stack);
        if (addTooltip(stack, lines, model)) {
            addPermissions(lines, model);
        }
    }

    protected boolean addTooltip(final ItemStack stack, final List<Component> lines, final M model) {
        return true;
    }

    private void addPermissions(final List<Component> lines, final SecurityCardModel model) {
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
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            final M model = createModel(stack);
            use(hand, serverPlayer, stack, model);
        }
        return InteractionResultHolder.consume(stack);
    }

    private void use(final InteractionHand hand, final ServerPlayer player, final ItemStack stack, final M model) {
        if (player.isCrouching()) {
            tryClear(player, model);
            return;
        }
        Platform.INSTANCE.getMenuOpener().openMenu(player, createMenuProvider(
            PlatformApi.INSTANCE.createInventorySlotReference(player, hand),
            model
        ));
    }

    void tryClear(final ServerPlayer player, final M model) {
        model.clear();
        player.sendSystemMessage(createTranslation("item", "security_card.cleared"));
    }

    abstract M createModel(ItemStack stack);

    abstract AbstractSecurityCardExtendedMenuProvider createMenuProvider(
        SlotReference slotReference,
        M model
    );
}
