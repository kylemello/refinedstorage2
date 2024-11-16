package com.refinedmods.refinedstorage.common.autocrafting.monitor;

import com.refinedmods.refinedstorage.api.autocrafting.TaskId;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatusListener;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatusProvider;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

class TaskStatusProviderImpl implements TaskStatusProvider {
    private static final Item[] ITEM_SET = new Item[] {
        Items.DIRT,
        Items.DIAMOND,
        Items.GOLD_INGOT,
        Items.ACACIA_WOOD,
        Items.BEEHIVE
    };

    private final List<TaskStatus> statuses = new ArrayList<>();
    private final Set<TaskStatusListener> listeners = new HashSet<>();
    private final Random r = new Random();
    private int ticks;

    @Override
    public List<TaskStatus> getStatuses() {
        return Collections.unmodifiableList(statuses);
    }

    @Override
    public void addListener(final TaskStatusListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final TaskStatusListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void cancel(final TaskId taskId) {
        final TaskStatus status = statuses.stream()
            .filter(s -> s.info().id().equals(taskId))
            .findFirst()
            .orElse(null);
        if (status != null) {
            statuses.remove(status);
            listeners.forEach(l -> l.taskRemoved(taskId));
        }
    }

    @Override
    public void cancelAll() {
        final List<TaskStatus> copy = new ArrayList<>(statuses);
        statuses.clear();
        copy.forEach(s -> listeners.forEach(l -> l.taskRemoved(s.info().id())));
    }

    void testTick() {
        if (ticks++ % 10 != 0) {
            return;
        }
        final int chance = r.nextInt(100);
        if (chance < 50) {
            final TaskStatus status = generateTaskStatus();
            statuses.add(status);
            listeners.forEach(l -> l.taskAdded(status));
        } else if (chance < 85 && !statuses.isEmpty()) {
            final int idx = r.nextInt(statuses.size());
            final TaskStatus status = statuses.get(idx);
            final TaskStatus updated = new TaskStatus(
                status.info(),
                r.nextFloat(),
                generateTaskStatus().items()
            );
            statuses.set(idx, updated);
            listeners.forEach(l -> l.taskStatusChanged(updated));
        } else if (chance > 85 && !statuses.isEmpty()) {
            final int idx = r.nextInt(statuses.size());
            final TaskStatus status = statuses.remove(idx);
            listeners.forEach(l -> l.taskRemoved(status.info().id()));
        }
    }

    private TaskStatus generateTaskStatus() {
        final TaskStatus.TaskInfo taskInfo = new TaskStatus.TaskInfo(
            new TaskId(UUID.randomUUID()),
            ItemResource.ofItemStack(new ItemStack(ITEM_SET[r.nextInt(ITEM_SET.length)])),
            5 + r.nextLong(32),
            System.currentTimeMillis()
        );
        final float pct = r.nextFloat();
        final List<TaskStatus.Item> items = new ArrayList<>();
        for (int i = 0; i < 3 + r.nextInt(30); i++) {
            final int typeIdx = r.nextInt(TaskStatus.ItemType.values().length);
            final TaskStatus.ItemType type = TaskStatus.ItemType.values()[typeIdx];
            final int idx = r.nextInt(5);
            final int amount = 2 + r.nextInt(20);
            items.add(new TaskStatus.Item(
                type,
                ItemResource.ofItemStack(new ItemStack(ITEM_SET[r.nextInt(ITEM_SET.length)])),
                idx == 0 ? amount : 0,
                idx == 1 ? amount : 0,
                idx == 2 ? amount : 0,
                idx == 3 ? amount : 0,
                idx == 4 ? amount : 0
            ));
        }
        return new TaskStatus(taskInfo, pct, items);
    }
}
