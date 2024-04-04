package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;
import com.refinedmods.refinedstorage2.api.network.security.SecurityPolicy;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.Platform;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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
import static java.util.Objects.requireNonNull;

public class SecurityCardItem extends AbstractSecurityCardItem {
    private static final String TAG_BOUND_PLAYER_ID = "bid";
    private static final String TAG_BOUND_PLAYER_NAME = "bname";

    private static final Component UNBOUND_HELP = createTranslation("item", "security_card.unbound.help");
    private static final Component BOUND_HELP = createTranslation("item", "security_card.bound.help");

    public SecurityCardItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(final ItemStack stack,
                                @Nullable final Level level,
                                final List<Component> lines,
                                final TooltipFlag flag) {
        final String boundPlayerName = getBoundPlayerName(stack);
        if (boundPlayerName == null) {
            lines.add(createTranslation("item", "security_card.unbound").withStyle(ChatFormatting.GRAY));
        } else {
            lines.add(createTranslation(
                "item",
                "security_card.bound",
                Component.literal(boundPlayerName).withStyle(ChatFormatting.YELLOW)
            ).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, lines, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer && !stack.hasTag()) {
            setBoundPlayer(serverPlayer, stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
        return Optional.of(new HelpTooltipComponent(isValid(stack) ? BOUND_HELP : UNBOUND_HELP));
    }

    @Override
    AbstractSecurityCardExtendedMenuProvider createMenuProvider(final SlotReference slotReference,
                                                                final SecurityPolicy policy,
                                                                final Set<PlatformPermission> dirtyPermissions,
                                                                final ItemStack stack) {
        return new SecurityCardExtendedMenuProvider(
            slotReference,
            policy,
            dirtyPermissions,
            requireNonNull(getBoundPlayerId(stack)),
            requireNonNull(getBoundPlayerName(stack))
        );
    }

    @Override
    public boolean isValid(final ItemStack stack) {
        return stack.getTag() != null
            && stack.getTag().contains(TAG_BOUND_PLAYER_ID)
            && stack.getTag().contains(TAG_BOUND_PLAYER_NAME);
    }

    @Override
    public Optional<SecurityActor> getActor(final ItemStack stack) {
        final UUID playerId = getBoundPlayerId(stack);
        if (playerId == null) {
            return Optional.empty();
        }
        return Optional.of(new PlayerSecurityActor(playerId));
    }

    @Override
    public long getEnergyUsage() {
        return Platform.INSTANCE.getConfig().getSecurityCard().getEnergyUsage();
    }

    @Nullable
    UUID getBoundPlayerId(final ItemStack stack) {
        return (stack.getTag() == null || !stack.getTag().contains(TAG_BOUND_PLAYER_ID))
            ? null
            : stack.getTag().getUUID(TAG_BOUND_PLAYER_ID);
    }

    @Nullable
    String getBoundPlayerName(final ItemStack stack) {
        return (stack.getTag() == null || !stack.getTag().contains(TAG_BOUND_PLAYER_NAME))
            ? null
            : stack.getTag().getString(TAG_BOUND_PLAYER_NAME);
    }

    void setBoundPlayer(final ServerPlayer player, final ItemStack stack) {
        final CompoundTag tag = stack.getOrCreateTag();
        tag.putUUID(TAG_BOUND_PLAYER_ID, player.getGameProfile().getId());
        tag.putString(TAG_BOUND_PLAYER_NAME, player.getGameProfile().getName());
    }
}
