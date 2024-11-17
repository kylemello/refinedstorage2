package com.refinedmods.refinedstorage.api.network.impl.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.PatternRepositoryImpl;
import com.refinedmods.refinedstorage.api.autocrafting.TaskId;
import com.refinedmods.refinedstorage.api.autocrafting.preview.Preview;
import com.refinedmods.refinedstorage.api.autocrafting.preview.PreviewItem;
import com.refinedmods.refinedstorage.api.autocrafting.preview.PreviewType;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatusListener;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatusProvider;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.autocrafting.ParentContainer;
import com.refinedmods.refinedstorage.api.network.autocrafting.PatternListener;
import com.refinedmods.refinedstorage.api.network.autocrafting.PatternProvider;
import com.refinedmods.refinedstorage.api.network.node.container.NetworkNodeContainer;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AutocraftingNetworkComponentImpl implements AutocraftingNetworkComponent, ParentContainer {
    private final Set<PatternProvider> providers = new HashSet<>();
    private final Set<PatternListener> listeners = new HashSet<>();
    private final PatternRepositoryImpl patternRepository = new PatternRepositoryImpl();
    private final TaskStatusProvider taskStatusProvider;

    public AutocraftingNetworkComponentImpl(final TaskStatusProvider taskStatusProvider) {
        this.taskStatusProvider = taskStatusProvider;
    }

    @Override
    public void onContainerAdded(final NetworkNodeContainer container) {
        if (container.getNode() instanceof PatternProvider provider) {
            provider.onAddedIntoContainer(this);
            providers.add(provider);
        }
    }

    @Override
    public void onContainerRemoved(final NetworkNodeContainer container) {
        if (container.getNode() instanceof PatternProvider provider) {
            provider.onRemovedFromContainer(this);
            providers.remove(provider);
        }
    }

    @Override
    public void add(final Pattern pattern) {
        patternRepository.add(pattern);
        listeners.forEach(listener -> listener.onAdded(pattern));
    }

    @Override
    public void remove(final Pattern pattern) {
        listeners.forEach(listener -> listener.onRemoved(pattern));
        patternRepository.remove(pattern);
    }

    @Override
    public Set<ResourceKey> getOutputs() {
        return patternRepository.getOutputs();
    }

    @Override
    public boolean contains(final AutocraftingNetworkComponent component) {
        return providers.stream().anyMatch(provider -> provider.contains(component));
    }

    @Override
    public Optional<Preview> getPreview(final ResourceKey resource, final long amount) {
        final List<PreviewItem> items = new ArrayList<>();
        final boolean missing = amount == 404;
        for (int i = 0; i < 31; ++i) {
            items.add(new PreviewItem(
                resource,
                (i + 1),
                (i % 2 == 0 && missing) ? amount : 0,
                i % 2 == 0 ? 0 : amount
            ));
        }
        return Optional.of(new Preview(missing
            ? PreviewType.MISSING_RESOURCES
            : PreviewType.SUCCESS, items));
    }

    @Override
    public boolean startTask(final ResourceKey resource, final long amount) {
        return true;
    }

    @Override
    public void addListener(final PatternListener listener) {
        listeners.add(listener);
    }

    @Override
    public void addListener(final TaskStatusListener listener) {
        taskStatusProvider.addListener(listener);
    }

    @Override
    public void removeListener(final PatternListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeListener(final TaskStatusListener listener) {
        taskStatusProvider.removeListener(listener);
    }

    @Override
    public Set<Pattern> getPatterns() {
        return patternRepository.getAll();
    }

    @Override
    public List<TaskStatus> getStatuses() {
        return taskStatusProvider.getStatuses();
    }

    @Override
    public void cancel(final TaskId taskId) {
        taskStatusProvider.cancel(taskId);
    }

    @Override
    public void cancelAll() {
        taskStatusProvider.cancelAll();
    }

    @Override
    public void testUpdate() {
        taskStatusProvider.testUpdate();
    }
}
