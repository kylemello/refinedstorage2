package com.refinedmods.refinedstorage2.platform.api.security;

import com.refinedmods.refinedstorage2.api.network.security.Permission;

import net.minecraft.network.chat.Component;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.5")
public interface PlatformPermission extends Permission {
    Component getName();

    Component getDescription();

    Component getOwnerName();
}
