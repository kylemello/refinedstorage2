package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;

import java.util.List;
import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

public class SecurityCardItem extends AbstractSecurityCardItem<PlayerSecurityCardModel> {
    private static final Component UNBOUND_HELP = createTranslation("item", "security_card.unbound.help");
    private static final Component BOUND_HELP = createTranslation("item", "security_card.bound.help");

    public SecurityCardItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    void tryClear(final ServerPlayer player, final PlayerSecurityCardModel model) {
        if (model.isCleared()) {
            bind(player, model);
            return;
        }
        super.tryClear(player, model);
    }

    private void bind(final ServerPlayer player, final PlayerSecurityCardModel model) {
        model.setBoundPlayer(player);
        player.sendSystemMessage(createTranslation(
            "item",
            "security_card.bound",
            Component.literal(player.getGameProfile().getName()).withStyle(ChatFormatting.YELLOW)
        ));
    }

    @Override
    protected boolean addTooltip(final ItemStack stack,
                                 final List<Component> lines,
                                 final PlayerSecurityCardModel model) {
        final String boundPlayerName = model.getBoundPlayerName();
        if (boundPlayerName == null) {
            lines.add(createTranslation("item", "security_card.unbound").withStyle(ChatFormatting.GRAY));
            return false;
        }
        lines.add(createTranslation(
            "item",
            "security_card.bound",
            Component.literal(boundPlayerName).withStyle(ChatFormatting.YELLOW)
        ).withStyle(ChatFormatting.GRAY));
        return true;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
        return Optional.of(new HelpTooltipComponent(isActive(stack) ? BOUND_HELP : UNBOUND_HELP));
    }

    @Override
    PlayerSecurityCardModel createModel(final ItemStack stack) {
        return new PlayerSecurityCardModel(stack);
    }

    @Override
    AbstractSecurityCardExtendedMenuProvider createMenuProvider(final SlotReference slotReference,
                                                                final PlayerSecurityCardModel model) {
        return new SecurityCardExtendedMenuProvider(slotReference, model);
    }

    boolean isActive(final ItemStack stack) {
        return PlayerSecurityCardModel.isActive(stack);
    }
}
