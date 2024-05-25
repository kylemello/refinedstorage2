package com.refinedmods.refinedstorage2.platform.forge;

import com.refinedmods.refinedstorage2.api.core.Action;
import com.refinedmods.refinedstorage2.api.network.Network;
import com.refinedmods.refinedstorage2.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage2.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage2.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage2.api.storage.EmptyActor;
import com.refinedmods.refinedstorage2.platform.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage2.platform.common.content.Blocks;
import com.refinedmods.refinedstorage2.platform.common.support.resource.ItemResource;

import java.util.Arrays;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class GameTestUtil {
    public static final ItemResource DIRT = ItemResource.ofItemStack(new ItemStack(Items.DIRT));
    public static final Blocks RSBLOCKS = Blocks.INSTANCE;

    private GameTestUtil() {
    }

    @Nullable
    public static Network getNetwork(final net.minecraft.gametest.framework.GameTestHelper helper, final BlockPos pos) {
        try {
            final var be = requireBlockEntity(helper, pos, AbstractNetworkNodeContainerBlockEntity.class);
            final var field = AbstractNetworkNodeContainerBlockEntity.class.getDeclaredField("mainNode");
            field.setAccessible(true);
            final NetworkNode mainNode = (NetworkNode) field.get(be);
            return mainNode.getNetwork();
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static Runnable itemIsInserted(final net.minecraft.gametest.framework.GameTestHelper helper,
                                          final BlockPos networkPos,
                                          final ItemResource resource,
                                          final long amount) {
        return () -> {
            final Network network = getNetwork(helper, networkPos);
            helper.assertTrue(network != null && network.getComponent(StorageNetworkComponent.class)
                    .insert(resource, amount, Action.EXECUTE, EmptyActor.INSTANCE) == amount,
                "Item couldn't be inserted"
            );
        };
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> T requireBlockEntity(
        final GameTestHelper helper,
        final BlockPos pos,
        final Class<T> clazz
    ) {
        final BlockEntity blockEntity = helper.getBlockEntity(pos);
        if (blockEntity == null) {
            throw new GameTestAssertException("Block entity not found at " + pos);
        }
        if (!clazz.isInstance(blockEntity)) {
            throw new GameTestAssertException(
                "Expected block entity of type " + clazz + " but was " + blockEntity.getClass()
            );
        }
        return (T) blockEntity;
    }

    public static Runnable storageMustContainExactly(final net.minecraft.gametest.framework.GameTestHelper helper,
                                                     final BlockPos networkPos,
                                                     final ResourceAmount... expected) {
        return () -> {
            final Network network = getNetwork(helper, networkPos);
            helper.assertTrue(network != null, "Network is not found");
            if (network == null) {
                return;
            }
            final StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
            for (final ResourceAmount expectedItem : expected) {
                final boolean contains = storage.getAll()
                    .stream()
                    .anyMatch(inStorage -> inStorage.getResource().equals(expectedItem.getResource())
                        && inStorage.getAmount() == expectedItem.getAmount());
                if (!contains) {
                    throw new GameTestAssertException("Missing from storage: " + expectedItem);
                }
            }
            for (final ResourceAmount inStorage : storage.getAll()) {
                final boolean wasExpected = Arrays.stream(expected).anyMatch(
                    expectedItem -> expectedItem.getResource().equals(inStorage.getResource())
                        && expectedItem.getAmount() == inStorage.getAmount()
                );
                if (!wasExpected) {
                    throw new GameTestAssertException("Unexpected in storage: " + inStorage);
                }
            }
        };
    }
}
