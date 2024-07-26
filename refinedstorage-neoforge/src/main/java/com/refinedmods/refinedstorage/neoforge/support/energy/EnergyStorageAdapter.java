package com.refinedmods.refinedstorage.neoforge.support.energy;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.energy.EnergyStorage;

import net.neoforged.neoforge.energy.IEnergyStorage;

public record EnergyStorageAdapter(EnergyStorage energyStorage) implements IEnergyStorage {
    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        return (int) energyStorage.receive(maxReceive, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        return (int) energyStorage.extract(maxExtract, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public int getEnergyStored() {
        return (int) energyStorage.getStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) energyStorage.getCapacity();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
