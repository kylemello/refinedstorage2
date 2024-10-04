package com.refinedmods.refinedstorage.common.util;

import com.refinedmods.refinedstorage.common.autocrafting.preview.AutocraftingPreview;
import com.refinedmods.refinedstorage.common.autocrafting.preview.AutocraftingPreviewScreen;

import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public final class ClientPlatformUtil {
    private static final SystemToast.SystemToastId NO_PERMISSION_TOAST_ID = new SystemToast.SystemToastId();
    private static final MutableComponent NO_PERMISSION = createTranslation("misc", "no_permission");

    private ClientPlatformUtil() {
    }

    @Nullable
    public static Level getClientLevel() { // avoids classloading issues
        return Minecraft.getInstance().level;
    }

    public static void addNoPermissionToast(final Component message) {
        SystemToast.add(
            Minecraft.getInstance().getToasts(),
            NO_PERMISSION_TOAST_ID,
            NO_PERMISSION,
            message
        );
    }

    public static void craftingPreviewReceived(final UUID id, final AutocraftingPreview preview) {
        if (Minecraft.getInstance().screen instanceof AutocraftingPreviewScreen screen) {
            screen.getMenu().previewReceived(id, preview);
        }
    }
}
