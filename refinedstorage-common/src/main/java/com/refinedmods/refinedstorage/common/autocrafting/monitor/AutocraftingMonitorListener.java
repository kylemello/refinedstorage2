package com.refinedmods.refinedstorage.common.autocrafting.monitor;

import com.refinedmods.refinedstorage.api.autocrafting.status.AutocraftingTaskStatus;

import java.util.List;
import javax.annotation.Nullable;

@FunctionalInterface
interface AutocraftingMonitorListener {
    void taskChanged(@Nullable AutocraftingTaskStatus.Id id,
                     List<AutocraftingTaskStatus.Element> elements);
}
