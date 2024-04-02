package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.api.network.security.Permission;
import com.refinedmods.refinedstorage2.api.network.security.SecurityPolicy;
import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.api.security.SecurityPolicyContainerItem;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.Platform;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

abstract class AbstractSecurityCardItem extends Item implements SecurityPolicyContainerItem {
    private static final String TAG_PERMISSIONS = "permissions";

    protected AbstractSecurityCardItem(final Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(final ItemStack stack,
                                @Nullable final Level level,
                                final List<Component> lines,
                                final TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        getPolicy(stack).ifPresent(policy -> appendHoverText(lines, policy, getDirtyPermissions(stack)));
    }

    private void appendHoverText(final List<Component> lines,
                                 final SecurityPolicy policy,
                                 final Set<PlatformPermission> dirtyPermissions) {
        final List<PlatformPermission> allPermissions = PlatformApi.INSTANCE.getPermissionRegistry().getAll();
        allPermissions.forEach(permission -> {
            final boolean allowed = policy.isAllowed(permission);
            final boolean dirty = dirtyPermissions.contains(permission);
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
            doUse(hand, serverPlayer, stack);
        }
        return InteractionResultHolder.consume(stack);
    }

    private void doUse(final InteractionHand hand, final ServerPlayer player, final ItemStack stack) {
        if (player.isCrouching()) {
            clearConfiguration(player, stack);
            return;
        }
        getPolicy(stack).ifPresent(policy -> {
            final Set<PlatformPermission> dirtyPermissions = getDirtyPermissions(stack);
            Platform.INSTANCE.getMenuOpener().openMenu(player, createMenuProvider(
                PlatformApi.INSTANCE.createInventorySlotReference(player, hand),
                policy,
                dirtyPermissions,
                stack
            ));
        });
    }

    private void clearConfiguration(final ServerPlayer player, final ItemStack stack) {
        stack.setTag(null);
        player.sendSystemMessage(createTranslation("item", "security_card.cleared_configuration"));
    }

    abstract AbstractSecurityCardExtendedMenuProvider createMenuProvider(SlotReference slotReference,
                                                                         SecurityPolicy policy,
                                                                         Set<PlatformPermission> dirtyPermissions,
                                                                         ItemStack stack);

    @Override
    public Optional<SecurityPolicy> getPolicy(final ItemStack stack) {
        if (!isValid(stack)) {
            return Optional.empty();
        }
        if (stack.getTag() == null || !stack.getTag().contains(TAG_PERMISSIONS)) {
            return Optional.of(PlatformApi.INSTANCE.createDefaultSecurityPolicy());
        }
        final CompoundTag permissionsTag = stack.getTag().getCompound(TAG_PERMISSIONS);
        return Optional.of(createPolicy(permissionsTag));
    }

    private SecurityPolicy createPolicy(final CompoundTag permissionsTag) {
        final Set<Permission> allowedPermissions = new HashSet<>();
        for (final PlatformPermission permission : PlatformApi.INSTANCE.getPermissionRegistry().getAll()) {
            final ResourceLocation permissionId = PlatformApi.INSTANCE.getPermissionRegistry()
                .getId(permission)
                .orElseThrow();
            final boolean dirty = permissionsTag.contains(permissionId.toString());
            final boolean didExplicitlyAllow = dirty && permissionsTag.getBoolean(permissionId.toString());
            final boolean isAllowedByDefault = !dirty && permission.isAllowedByDefault();
            if (didExplicitlyAllow || isAllowedByDefault) {
                allowedPermissions.add(permission);
            }
        }
        return new SecurityPolicy(allowedPermissions);
    }

    @Override
    public boolean isValid(final ItemStack stack) {
        return true;
    }

    Set<PlatformPermission> getDirtyPermissions(final ItemStack stack) {
        if (stack.getTag() == null || !stack.getTag().contains(TAG_PERMISSIONS)) {
            return Collections.emptySet();
        }
        final CompoundTag permissionsTag = stack.getTag().getCompound(TAG_PERMISSIONS);
        return permissionsTag.getAllKeys()
            .stream()
            .map(ResourceLocation::new)
            .flatMap(id -> PlatformApi.INSTANCE.getPermissionRegistry().get(id).stream())
            .collect(Collectors.toSet());
    }

    void setPermission(final ItemStack stack, final ResourceLocation permissionId, final boolean allowed) {
        final CompoundTag permissionsTag = stack.getOrCreateTagElement(TAG_PERMISSIONS);
        permissionsTag.putBoolean(permissionId.toString(), allowed);
    }

    void resetPermission(final ItemStack stack, final ResourceLocation permissionId) {
        final CompoundTag permissionsTag = stack.getOrCreateTagElement(TAG_PERMISSIONS);
        permissionsTag.remove(permissionId.toString());
    }
}
