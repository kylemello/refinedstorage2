package com.refinedmods.refinedstorage.api.resource.list;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.apiguardian.api.API;

/**
 * This is a utility class to easily decorate a {@link ResourceList}.
 */
@API(status = API.Status.STABLE, since = "2.0.0-milestone.1.2")
public abstract class AbstractProxyResourceList implements ResourceList {
    private final ResourceList delegate;

    protected AbstractProxyResourceList(final ResourceList delegate) {
        this.delegate = delegate;
    }

    @Override
    public OperationResult add(final ResourceKey resource, final long amount) {
        return delegate.add(resource, amount);
    }

    @Override
    public Optional<OperationResult> remove(final ResourceKey resource, final long amount) {
        return delegate.remove(resource, amount);
    }

    @Override
    public Collection<ResourceAmount> getAll() {
        return delegate.getAll();
    }

    @Override
    public long getAmount(final ResourceKey resource) {
        return delegate.getAmount(resource);
    }

    @Override
    public boolean contains(final ResourceKey resource) {
        return delegate.contains(resource);
    }

    @Override
    public Set<ResourceKey> getResources() {
        return delegate.getResources();
    }

    @Override
    public ResourceList copy() {
        return delegate.copy();
    }

    @Override
    public void clear() {
        delegate.clear();
    }
}
