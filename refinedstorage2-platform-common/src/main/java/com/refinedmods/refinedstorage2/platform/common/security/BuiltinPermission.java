package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.security.BuiltinPermissions;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.common.content.ContentNames;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

public enum BuiltinPermission implements PlatformPermission {
    /**
     * Whether the player can insert resources into a network.
     */
    INSERT("insert"),
    /**
     * Whether the player can extract resources from a network.
     */
    EXTRACT("extract"),
    /**
     * Whether the player can start, cancel or view an autocrafting task.
     */
    AUTOCRAFTING("autocrafting"),
    /**
     * Whether the player can open network device GUIs.
     */
    MODIFY("modify"),
    /**
     * Whether the player can add or remove network devices.
     */
    BUILD("build"),
    /**
     * Whether the player can manage the security options for a network.
     */
    SECURITY("security");

    public static final BuiltinPermissions VIEW = new BuiltinPermissions(
        INSERT,
        EXTRACT,
        AUTOCRAFTING,
        MODIFY,
        BUILD,
        SECURITY
    );

    private final ResourceLocation id;
    private final Component name;
    private final Component description;

    BuiltinPermission(final String id) {
        this.id = createIdentifier(id);
        this.name = createTranslation("permission", id);
        this.description = createTranslation("permission", id + ".description");
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public Component getDescription() {
        return description;
    }

    @Override
    public Component getOwnerName() {
        return ContentNames.MOD;
    }

    @Override
    public boolean isAllowedByDefault() {
        return true;
    }
}
