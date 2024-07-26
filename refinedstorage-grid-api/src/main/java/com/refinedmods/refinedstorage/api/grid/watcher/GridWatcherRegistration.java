package com.refinedmods.refinedstorage.api.grid.watcher;

import com.refinedmods.refinedstorage.api.resource.list.listenable.ResourceListListener;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;

import javax.annotation.Nullable;

class GridWatcherRegistration {
    private final GridWatcher watcher;
    private final Class<? extends Actor> actorType;
    @Nullable
    private ResourceListListener listener;

    GridWatcherRegistration(final GridWatcher watcher, final Class<? extends Actor> actorType) {
        this.watcher = watcher;
        this.actorType = actorType;
    }

    void attach(final RootStorage rootStorage, final boolean replay) {
        this.listener = change -> watcher.onChanged(
            change.resourceAmount().getResource(),
            change.change(),
            rootStorage.findTrackedResourceByActorType(
                change.resourceAmount().getResource(),
                actorType
            ).orElse(null)
        );
        rootStorage.addListener(listener);
        if (replay) {
            rootStorage.getAll().forEach(resourceAmount -> watcher.onChanged(
                resourceAmount.getResource(),
                resourceAmount.getAmount(),
                rootStorage.findTrackedResourceByActorType(
                    resourceAmount.getResource(),
                    actorType
                ).orElse(null)
            ));
        }
    }

    void detach(final RootStorage rootStorage) {
        if (listener == null) {
            return;
        }
        rootStorage.removeListener(listener);
        listener = null;
    }
}
