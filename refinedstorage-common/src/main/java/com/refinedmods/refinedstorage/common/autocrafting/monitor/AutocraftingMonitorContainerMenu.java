package com.refinedmods.refinedstorage.common.autocrafting.monitor;

import com.refinedmods.refinedstorage.api.autocrafting.status.AutocraftingTaskStatus;
import com.refinedmods.refinedstorage.common.content.Menus;
import com.refinedmods.refinedstorage.common.support.AbstractBaseContainerMenu;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class AutocraftingMonitorContainerMenu extends AbstractBaseContainerMenu {
    private final Map<AutocraftingTaskStatus.Id, List<AutocraftingTaskStatus.Element>> elementsByTaskId;
    private final List<AutocraftingTaskStatus.Id> tasks;
    private final List<AutocraftingTaskStatus.Id> tasksView;

    @Nullable
    private AutocraftingMonitorListener listener;

    @Nullable
    private AutocraftingTaskStatus.Id currentTaskId;

    public AutocraftingMonitorContainerMenu(final int syncId, final AutocraftingMonitorData data) {
        super(Menus.INSTANCE.getAutocraftingMonitor(), syncId);
        this.elementsByTaskId = data.statuses().stream().collect(Collectors.toMap(
            AutocraftingTaskStatus::id,
            AutocraftingTaskStatus::elements
        ));
        this.tasks = data.statuses().stream().map(AutocraftingTaskStatus::id).collect(Collectors.toList());
        this.tasksView = Collections.unmodifiableList(tasks);
        this.currentTaskId = data.statuses().isEmpty() ? null : data.statuses().getFirst().id();
    }

    AutocraftingMonitorContainerMenu(final int syncId, final AutocraftingMonitorBlockEntity autocraftingMonitor) {
        super(Menus.INSTANCE.getAutocraftingMonitor(), syncId);
        this.elementsByTaskId = Collections.emptyMap();
        this.tasks = Collections.emptyList();
        this.tasksView = Collections.emptyList();
        this.currentTaskId = null;
    }

    void setListener(@Nullable final AutocraftingMonitorListener listener) {
        this.listener = listener;
    }

    List<AutocraftingTaskStatus.Element> getCurrentElements() {
        return elementsByTaskId.getOrDefault(currentTaskId, Collections.emptyList());
    }

    List<AutocraftingTaskStatus.Id> getTasks() {
        return tasksView;
    }

    void setCurrentTaskId(@Nullable final AutocraftingTaskStatus.Id taskId) {
        this.currentTaskId = taskId;
        loadCurrentTask();
    }

    void loadCurrentTask() {
        if (listener != null) {
            listener.taskChanged(
                currentTaskId,
                elementsByTaskId.getOrDefault(currentTaskId, Collections.emptyList())
            );
        }
    }

    void cancelCurrentTask() {
        // todo
    }

    void cancelAllTasks() {
        // todo
    }
}
