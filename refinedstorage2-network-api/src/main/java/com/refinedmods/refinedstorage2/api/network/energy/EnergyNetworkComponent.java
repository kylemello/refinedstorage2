package com.refinedmods.refinedstorage2.api.network.energy;

import com.refinedmods.refinedstorage2.api.network.NetworkComponent;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.1.1")
public interface EnergyNetworkComponent extends NetworkComponent {
    long getStored();

    long getCapacity();

    long extract(long amount);
}
