package com.refinedmods.refinedstorage.common.constructordestructor;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.network.node.SchedulingMode;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.api.constructordestructor.ConstructorStrategy;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;

public class ConstructorNetworkNode extends SimpleNetworkNode {
    private final Actor actor = new NetworkNodeActor(this);
    private final List<ConstructorTask> tasks = new ArrayList<>();

    @Nullable
    private Player player;
    @Nullable
    private ConstructorStrategy strategy;
    @Nullable
    private SchedulingMode schedulingMode;

    ConstructorNetworkNode(final long energyUsage) {
        super(energyUsage);
    }

    @Override
    public void doWork() {
        super.doWork();
        if (network == null || !isActive() || schedulingMode == null) {
            return;
        }
        schedulingMode.execute(tasks);
    }

    void setPlayer(@Nullable final Player player) {
        this.player = player;
    }

    void setSchedulingMode(@Nullable final SchedulingMode schedulingMode) {
        this.schedulingMode = schedulingMode;
    }

    void setFilters(final List<ResourceKey> filters) {
        this.tasks.clear();
        this.tasks.addAll(filters.stream().map(ConstructorTask::new).toList());
    }

    void setStrategy(@Nullable final ConstructorStrategy strategy) {
        this.strategy = strategy;
    }

    private class ConstructorTask implements SchedulingMode.ScheduledTask {
        private final ResourceKey filter;

        private ConstructorTask(final ResourceKey filter) {
            this.filter = filter;
        }

        @Override
        public boolean run() {
            if (strategy == null || network == null || player == null) {
                return false;
            }
            strategy.apply(filter, actor, player, network);
            return true;
        }
    }
}
