package com.refinedmods.refinedstorage.common.autocrafting.monitor;

import com.refinedmods.refinedstorage.api.autocrafting.TaskId;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatusListener;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatusProvider;
import com.refinedmods.refinedstorage.common.content.Menus;
import com.refinedmods.refinedstorage.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage.common.support.packet.c2s.C2SPackets;
import com.refinedmods.refinedstorage.common.support.packet.s2c.S2CPackets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class AutocraftingMonitorContainerMenu extends AbstractBaseContainerMenu implements TaskStatusListener {
    private final Map<TaskId, TaskStatus> statusByTaskId;
    private final List<TaskStatus.TaskInfo> tasks;
    private final List<TaskStatus.TaskInfo> tasksView;
    @Nullable
    private final TaskStatusProvider taskStatusProvider;
    private final Player player;

    @Nullable
    private AutocraftingMonitorListener listener;

    @Nullable
    private TaskId currentTaskId;

    public AutocraftingMonitorContainerMenu(final int syncId,
                                            final Inventory playerInventory,
                                            final AutocraftingMonitorData data) {
        super(Menus.INSTANCE.getAutocraftingMonitor(), syncId);
        this.statusByTaskId = data.statuses().stream().collect(Collectors.toMap(
            s -> s.info().id(),
            s -> s
        ));
        this.tasks = data.statuses().stream().map(TaskStatus::info).collect(Collectors.toList());
        this.tasksView = Collections.unmodifiableList(tasks);
        this.currentTaskId = data.statuses().isEmpty() ? null : data.statuses().getFirst().info().id();
        this.taskStatusProvider = null;
        this.player = playerInventory.player;
    }

    AutocraftingMonitorContainerMenu(final int syncId,
                                     final Player player,
                                     final TaskStatusProvider taskStatusProvider) {
        super(Menus.INSTANCE.getAutocraftingMonitor(), syncId);
        this.statusByTaskId = Collections.emptyMap();
        this.tasks = Collections.emptyList();
        this.tasksView = Collections.emptyList();
        this.currentTaskId = null;
        this.taskStatusProvider = taskStatusProvider;
        this.player = player;
        taskStatusProvider.addListener(this);
    }

    @Override
    public void removed(final Player removedPlayer) {
        super.removed(removedPlayer);
        if (taskStatusProvider != null) {
            taskStatusProvider.removeListener(this);
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (taskStatusProvider instanceof TaskStatusProviderImpl taskStatusProviderImpl) {
            taskStatusProviderImpl.testTick();
        }
    }

    void setListener(@Nullable final AutocraftingMonitorListener listener) {
        this.listener = listener;
    }

    List<TaskStatus.Item> getCurrentItems() {
        final TaskStatus status = statusByTaskId.get(currentTaskId);
        if (status == null) {
            return Collections.emptyList();
        }
        return status.items();
    }

    List<TaskStatus.TaskInfo> getTasks() {
        return tasksView;
    }

    float getPercentageCompleted(final TaskId taskId) {
        final TaskStatus status = statusByTaskId.get(taskId);
        return status == null ? 0 : status.percentageCompleted();
    }

    void setCurrentTaskId(@Nullable final TaskId taskId) {
        this.currentTaskId = taskId;
        loadCurrentTask();
    }

    void loadCurrentTask() {
        if (listener != null) {
            listener.currentTaskChanged(currentTaskId == null ? null : statusByTaskId.get(currentTaskId));
        }
    }

    @Override
    public void taskStatusChanged(final TaskStatus status) {
        if (taskStatusProvider != null && player instanceof ServerPlayer serverPlayer) {
            S2CPackets.sendAutocraftingMonitorTaskStatusChanged(serverPlayer, status);
            return;
        }
        statusByTaskId.put(status.info().id(), status);
    }

    @Override
    public void taskRemoved(final TaskId id) {
        if (taskStatusProvider != null && player instanceof ServerPlayer serverPlayer) {
            S2CPackets.sendAutocraftingMonitorTaskRemoved(serverPlayer, id);
            return;
        }
        statusByTaskId.remove(id);
        tasks.removeIf(task -> task.id().equals(id));
        if (listener != null) {
            listener.taskRemoved(id);
        }
        if (id.equals(currentTaskId)) {
            currentTaskId = tasks.isEmpty() ? null : tasks.getFirst().id();
            loadCurrentTask();
        }
    }

    @Override
    public void taskAdded(final TaskStatus status) {
        if (taskStatusProvider != null && player instanceof ServerPlayer serverPlayer) {
            S2CPackets.sendAutocraftingMonitorTaskAdded(serverPlayer, status);
            return;
        }
        statusByTaskId.put(status.info().id(), status);
        tasks.add(status.info());
        if (listener != null) {
            listener.taskAdded(status);
        }
        if (currentTaskId == null) {
            currentTaskId = status.info().id();
            loadCurrentTask();
        }
    }

    public void cancelTask(final TaskId taskId) {
        if (taskStatusProvider != null) {
            taskStatusProvider.cancel(taskId);
        }
    }

    void cancelCurrentTask() {
        if (currentTaskId != null) {
            C2SPackets.sendAutocraftingMonitorCancel(currentTaskId);
        }
    }

    public void cancelAllTasks() {
        if (taskStatusProvider != null) {
            taskStatusProvider.cancelAll();
        } else {
            C2SPackets.sendAutocraftingMonitorCancelAll();
        }
    }
}
