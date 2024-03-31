package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

public class FallbackSecurityCardItem extends AbstractSecurityCardItem<SecurityCardModel> {
    private static final Component HELP = createTranslation("item", "fallback_security_card.help");

    public FallbackSecurityCardItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    SecurityCardModel createModel(final ItemStack stack) {
        return new SecurityCardModel(stack);
    }

    @Override
    AbstractSecurityCardExtendedMenuProvider createMenuProvider(final SlotReference slotReference,
                                                                final SecurityCardModel model) {
        return new FallbackSecurityCardExtendedMenuProvider(slotReference, model);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
        return Optional.of(new HelpTooltipComponent(HELP));
    }
}
