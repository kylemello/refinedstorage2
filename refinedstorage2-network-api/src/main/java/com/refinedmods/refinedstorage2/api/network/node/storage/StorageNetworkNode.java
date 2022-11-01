package com.refinedmods.refinedstorage2.api.network.node.storage;

import com.refinedmods.refinedstorage2.api.network.component.StorageProvider;
import com.refinedmods.refinedstorage2.api.network.node.AbstractStorageNetworkNode;
import com.refinedmods.refinedstorage2.api.storage.Storage;
import com.refinedmods.refinedstorage2.api.storage.StorageRepository;
import com.refinedmods.refinedstorage2.api.storage.channel.StorageChannelType;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StorageNetworkNode<T> extends AbstractStorageNetworkNode implements StorageProvider {
    public static final Logger LOGGER = LogManager.getLogger();

    private final long energyUsage;
    private final StorageChannelType<?> type;
    private final NetworkNodeStorage<T> exposedStorage = new NetworkNodeStorage<>(this);

    @Nullable
    private Storage<T> internalStorage;

    public StorageNetworkNode(final long energyUsage, final StorageChannelType<T> type) {
        this.energyUsage = energyUsage;
        this.type = type;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void initializeExistingStorage(final StorageRepository storageRepository, final UUID storageId) {
        storageRepository.get(storageId).ifPresentOrElse(
            existingStorage -> {
                LOGGER.info("Loaded existing storage {}", storageId);
                this.internalStorage = (Storage) existingStorage;
            },
            () -> LOGGER.warn("Storage {} was not found, ignoring", storageId)
        );
    }

    public void initializeNewStorage(final StorageRepository storageRepository,
                                     final Storage<T> newStorage,
                                     final UUID storageId) {
        LOGGER.info("Loaded new storage {}", storageId);
        storageRepository.set(storageId, newStorage);
        this.internalStorage = newStorage;
    }

    @Override
    protected void onActiveChanged(final boolean newActive) {
        super.onActiveChanged(newActive);
        if (network == null || internalStorage == null) {
            return;
        }
        LOGGER.info("Storage activeness got changed to '{}', updating underlying storage", newActive);
        if (newActive) {
            exposedStorage.setSource(internalStorage);
        } else {
            exposedStorage.removeSource();
        }
    }

    @Override
    public long getEnergyUsage() {
        return energyUsage;
    }

    public long getStored() {
        return exposedStorage.getStored();
    }

    public long getCapacity() {
        return exposedStorage.getCapacity();
    }

    @Override
    protected Set<StorageChannelType<?>> getRelevantStorageChannelTypes() {
        return Set.of(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> Optional<Storage<S>> getStorageForChannel(final StorageChannelType<S> channelType) {
        if (channelType == this.type) {
            return Optional.of((Storage<S>) exposedStorage);
        }
        return Optional.empty();
    }
}
