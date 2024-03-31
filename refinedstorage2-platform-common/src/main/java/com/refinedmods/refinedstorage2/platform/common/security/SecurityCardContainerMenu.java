package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.Platform;
import com.refinedmods.refinedstorage2.platform.common.content.Menus;
import com.refinedmods.refinedstorage2.platform.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage2.platform.common.support.stretching.ScreenSizeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class SecurityCardContainerMenu extends AbstractBaseContainerMenu implements ScreenSizeListener {
    private final Inventory playerInventory;
    private final List<Permission> permissions = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();

    @Nullable
    private Player boundTo;

    public SecurityCardContainerMenu(final int syncId,
                                     final Inventory playerInventory,
                                     final FriendlyByteBuf buf) {
        super(Menus.INSTANCE.getSecurityCard(), syncId);
        this.playerInventory = playerInventory;

        this.disabledSlot = PlatformApi.INSTANCE.getSlotReference(buf).orElse(null);

        final int amountOfPermissions = buf.readInt();
        for (int i = 0; i < amountOfPermissions; ++i) {
            final ResourceLocation id = buf.readResourceLocation();
            final boolean allowed = buf.readBoolean();
            final boolean dirty = buf.readBoolean();
            PlatformApi.INSTANCE.getPermissionRegistry().get(id).ifPresent(permission -> permissions.add(new Permission(
                id,
                permission.getName(),
                permission.getDescription(),
                permission.getOwnerName(),
                allowed,
                dirty
            )));
        }

        if (buf.readBoolean()) {
            this.boundTo = new Player(buf.readUUID(), buf.readUtf());
        }

        final int amountOfPlayers = buf.readInt();
        for (int i = 0; i < amountOfPlayers; ++i) {
            final UUID id = buf.readUUID();
            final String name = buf.readUtf();
            players.add(new Player(id, name));
        }
    }

    SecurityCardContainerMenu(final int syncId, final Inventory playerInventory, final SlotReference disabledSlot) {
        super(Menus.INSTANCE.getSecurityCard(), syncId);
        this.playerInventory = playerInventory;
        this.disabledSlot = disabledSlot;
    }

    List<Permission> getPermissions() {
        return permissions;
    }

    List<Player> getPlayers() {
        return players;
    }

    @Nullable
    Player getBoundTo() {
        return boundTo;
    }

    @Override
    public void initSlots(final int playerInventoryY) {
        resetSlots();
        addPlayerInventory(playerInventory, 8, playerInventoryY);
    }

    public void setPermission(final ResourceLocation permissionId, final boolean allowed) {
        if (disabledSlot == null) {
            return;
        }
        disabledSlot.resolve(playerInventory.player).ifPresent(stack -> setPermission(permissionId, allowed, stack));
    }

    private void setPermission(final ResourceLocation permissionId, final boolean allowed, final ItemStack stack) {
        if (stack.getItem() instanceof SecurityCardItem securityCardItem) {
            securityCardItem.getModel(stack).setPermission(permissionId, allowed);
        }
    }

    public void resetPermissionServer(final ResourceLocation permissionId) {
        if (disabledSlot == null) {
            return;
        }
        disabledSlot.resolve(playerInventory.player).ifPresent(stack -> resetPermissionServer(permissionId, stack));
    }

    private void resetPermissionServer(final ResourceLocation permissionId, final ItemStack stack) {
        if (stack.getItem() instanceof SecurityCardItem securityCardItem) {
            securityCardItem.getModel(stack).resetPermission(permissionId);
        }
    }

    public void setBoundPlayer(final MinecraftServer server, @Nullable final UUID playerId) {
        if (disabledSlot == null) {
            return;
        }
        disabledSlot.resolve(playerInventory.player).ifPresent(stack -> setBoundPlayer(server, playerId, stack));
    }

    private void setBoundPlayer(final MinecraftServer server, @Nullable final UUID playerId, final ItemStack stack) {
        if (stack.getItem() instanceof SecurityCardItem securityCardItem) {
            final ServerPlayer player = playerId == null ? null : server.getPlayerList().getPlayer(playerId);
            securityCardItem.getModel(stack).setBoundPlayer(player);
        }
    }

    Permission changePermission(final ResourceLocation permissionId, final boolean selected) {
        Platform.INSTANCE.getClientToServerCommunications().sendSecurityCardPermission(permissionId, selected);
        return updatePermissionLocally(permissionId, selected, true);
    }

    Permission resetPermission(final ResourceLocation permissionId) {
        final PlatformPermission permission = PlatformApi.INSTANCE.getPermissionRegistry()
            .get(permissionId)
            .orElseThrow();
        final boolean allowed = permission.isAllowedByDefault();
        Platform.INSTANCE.getClientToServerCommunications().sendSecurityCardResetPermission(permissionId);
        return updatePermissionLocally(permissionId, allowed, false);
    }

    private Permission updatePermissionLocally(final ResourceLocation permissionId,
                                               final boolean allowed,
                                               final boolean dirty) {
        final Permission localPermission = permissions.stream().filter(p -> p.id().equals(permissionId))
            .findFirst()
            .orElseThrow();
        final int index = permissions.indexOf(localPermission);
        final Permission updatedLocalPermission = new Permission(
            localPermission.id(),
            localPermission.name(),
            localPermission.description(),
            localPermission.ownerName(),
            allowed,
            dirty
        );
        permissions.set(index, updatedLocalPermission);
        return updatedLocalPermission;
    }

    void changeBoundPlayer(@Nullable final Player player) {
        Platform.INSTANCE.getClientToServerCommunications().sendSecurityCardBoundPlayer(
            player == null ? null : player.id()
        );
        this.boundTo = player;
    }

    record Permission(ResourceLocation id,
                      Component name,
                      Component description,
                      Component ownerName,
                      boolean allowed,
                      boolean dirty) {
    }

    record Player(UUID id, String name) {
    }
}
