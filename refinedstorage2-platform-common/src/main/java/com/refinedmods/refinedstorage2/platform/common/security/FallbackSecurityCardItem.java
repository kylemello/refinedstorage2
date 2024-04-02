package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;
import com.refinedmods.refinedstorage2.api.network.security.SecurityPolicy;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;

import java.util.Optional;
import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

public class FallbackSecurityCardItem extends AbstractSecurityCardItem {
    private static final Component HELP = createTranslation("item", "fallback_security_card.help");

    public FallbackSecurityCardItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    AbstractSecurityCardExtendedMenuProvider createMenuProvider(final SlotReference slotReference,
                                                                final SecurityPolicy policy,
                                                                final Set<PlatformPermission> dirtyPermissions,
                                                                final ItemStack stack) {
        return new FallbackSecurityCardExtendedMenuProvider(slotReference, policy, dirtyPermissions, slotReference);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
        return Optional.of(new HelpTooltipComponent(HELP));
    }

    @Override
    public Optional<SecurityActor> getActor(final ItemStack stack) {
        return Optional.empty();
    }
}
