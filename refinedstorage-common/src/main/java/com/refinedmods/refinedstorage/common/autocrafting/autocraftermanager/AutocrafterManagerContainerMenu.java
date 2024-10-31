package com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.content.Menus;
import com.refinedmods.refinedstorage.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.support.packet.s2c.S2CPackets;
import com.refinedmods.refinedstorage.common.support.stretching.ScreenSizeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AutocrafterManagerContainerMenu extends AbstractBaseContainerMenu implements ScreenSizeListener,
    AutocrafterManagerWatcher {
    private final Inventory playerInventory;
    private final List<Group> groups;
    private final List<AutocrafterManagerSlot> autocrafterSlots = new ArrayList<>();

    @Nullable
    private AutocrafterManagerListener listener;
    @Nullable
    private AutocrafterManagerBlockEntity autocrafterManager;
    private String query = "";
    private boolean active;

    public AutocrafterManagerContainerMenu(final int syncId,
                                           final Inventory playerInventory,
                                           final AutocrafterManagerData data) {
        super(Menus.INSTANCE.getAutocrafterManager(), syncId);
        this.playerInventory = playerInventory;
        registerProperty(new ClientProperty<>(PropertyTypes.REDSTONE_MODE, RedstoneMode.IGNORE));
        this.groups = data.groups().stream().map(group -> Group.from(playerInventory.player.level(), group)).toList();
        this.active = data.active();
        resized(0, 0, 0);
    }

    public AutocrafterManagerContainerMenu(final int syncId,
                                           final Inventory playerInventory,
                                           final AutocrafterManagerBlockEntity autocrafterManager,
                                           final List<Container> containers) {
        super(Menus.INSTANCE.getAutocrafterManager(), syncId);
        this.playerInventory = playerInventory;
        this.autocrafterManager = autocrafterManager;
        this.autocrafterManager.addWatcher(this);
        registerProperty(new ServerProperty<>(
            PropertyTypes.REDSTONE_MODE,
            autocrafterManager::getRedstoneMode,
            autocrafterManager::setRedstoneMode
        ));
        this.groups = Collections.emptyList();
        addServerSideSlots(containers);
    }

    @Override
    public void removed(final Player playerEntity) {
        super.removed(playerEntity);
        if (autocrafterManager != null) {
            autocrafterManager.removeWatcher(this);
        }
    }

    void setListener(final AutocrafterManagerListener listener) {
        this.listener = listener;
    }

    void setQuery(final String query) {
        this.query = query;
        notifyListener();
    }

    private void notifyListener() {
        if (listener != null) {
            listener.slotsChanged();
        }
    }

    private void addServerSideSlots(final List<Container> containers) {
        for (final Container container : containers) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                addSlot(new Slot(container, i, 0, 0));
            }
        }
        addPlayerInventory(playerInventory, 0, 0);
    }

    public boolean containsPattern(final ItemStack stack) {
        for (final Slot slot : autocrafterSlots) {
            if (slot.getItem() == stack) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void resized(final int playerInventoryY, final int topYStart, final int topYEnd) {
        addSlots(playerInventoryY, topYStart, topYEnd);
    }

    private void addSlots(final int playerInventoryY, final int topYStart, final int topYEnd) {
        resetSlots();
        autocrafterSlots.clear();
        final int rowX = 7 + 1;
        final int startY = topYStart - 18;
        int rowY = topYStart;
        for (final Group group : getGroups()) {
            rowY += addSlots(group, rowX, rowY, startY, topYEnd);
        }
        addPlayerInventory(playerInventory, 8, playerInventoryY);
    }

    private int addSlots(final Group group, final int rowX, final int rowY, final int startY, final int topYEnd) {
        int j = 0;
        for (int i = 0; i < group.slotCount; i++) {
            final int slotX = rowX + ((j % 9) * 18);
            final int slotY = rowY + 18 + ((j / 9) * 18);
            final boolean visible = active && isSlotVisible(group, i);
            final AutocrafterManagerSlot slot = new AutocrafterManagerSlot(
                group.backingInventory,
                i,
                slotX,
                slotY,
                startY,
                topYEnd,
                visible
            );
            addSlot(slot);
            if (visible) {
                autocrafterSlots.add(slot);
                ++j;
            }
        }
        group.visibleSlots = j;
        if (j == 0) {
            return 0;
        }
        return (group.getVisibleRows() + 1) * 18;
    }

    private boolean isSlotVisible(final Group group, final int index) {
        final String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);
        if (normalizedQuery.isEmpty()) {
            return true;
        }
        return getSearchMode().isSlotVisible(group, normalizedQuery, index);
    }

    List<Group> getGroups() {
        return groups;
    }

    List<AutocrafterManagerSlot> getAutocrafterSlots() {
        return autocrafterSlots;
    }

    AutocrafterManagerSearchMode getSearchMode() {
        return Platform.INSTANCE.getConfig().getAutocrafterManager().getSearchMode();
    }

    void setSearchMode(final AutocrafterManagerSearchMode searchMode) {
        Platform.INSTANCE.getConfig().getAutocrafterManager().setSearchMode(searchMode);
        notifyListener();
    }

    AutocrafterManagerViewType getViewType() {
        return Platform.INSTANCE.getConfig().getAutocrafterManager().getViewType();
    }

    void setViewType(final AutocrafterManagerViewType toggle) {
        Platform.INSTANCE.getConfig().getAutocrafterManager().setViewType(toggle);
        notifyListener();
    }

    public void setActive(final boolean active) {
        this.active = active;
        notifyListener();
    }

    boolean isActive() {
        return active;
    }

    @Override
    public void activeChanged(final boolean newActive) {
        if (playerInventory.player instanceof ServerPlayer serverPlayerEntity) {
            S2CPackets.sendAutocrafterManagerActive(serverPlayerEntity, newActive);
        }
    }

    static class Group {
        final Component name;
        final int slotCount;
        final Container backingInventory;

        private final Level level;
        private int visibleSlots;

        Group(final Level level, final Component name, final int slotCount, final Container backingInventory) {
            this.level = level;
            this.name = name;
            this.slotCount = slotCount;
            this.backingInventory = backingInventory;
        }

        private static Group from(final Level level, final AutocrafterManagerData.Group group) {
            return new Group(level, group.name(), group.slotCount(), new SimpleContainer(group.slotCount()));
        }

        boolean isVisible() {
            return visibleSlots > 0;
        }

        int getVisibleRows() {
            return Math.ceilDiv(visibleSlots, 9);
        }

        int getVisibleSlots() {
            return visibleSlots;
        }

        boolean nameContains(final String normalizedQuery) {
            return name.getString().toLowerCase(Locale.ROOT).trim().contains(normalizedQuery);
        }

        boolean hasPatternInput(final String normalizedQuery, final int index) {
            final ItemStack patternStack = backingInventory.getItem(index);
            return RefinedStorageApi.INSTANCE.getPattern(patternStack, level).map(
                pattern -> hasResource(pattern.getInputResources(), normalizedQuery)
            ).orElse(false);
        }

        boolean hasPatternOutput(final String normalizedQuery, final int index) {
            final ItemStack patternStack = backingInventory.getItem(index);
            return RefinedStorageApi.INSTANCE.getPattern(patternStack, level).map(
                pattern -> hasResource(pattern.getOutputResources(), normalizedQuery)
            ).orElse(false);
        }

        private static boolean hasResource(final Set<ResourceKey> resources, final String normalizedQuery) {
            return resources.stream().anyMatch(key ->
                RefinedStorageApi.INSTANCE.getResourceRendering(key.getClass())
                    .getDisplayName(key)
                    .getString()
                    .toLowerCase(Locale.ROOT)
                    .trim()
                    .contains(normalizedQuery));
        }
    }
}
