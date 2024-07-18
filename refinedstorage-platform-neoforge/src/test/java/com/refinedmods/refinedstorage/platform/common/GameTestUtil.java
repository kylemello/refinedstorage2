package com.refinedmods.refinedstorage.platform.common;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.EmptyActor;
import com.refinedmods.refinedstorage.platform.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.platform.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.platform.common.content.Blocks;
import com.refinedmods.refinedstorage.platform.common.content.Items;
import com.refinedmods.refinedstorage.platform.common.iface.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.platform.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.platform.common.support.resource.ItemResource;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

public final class GameTestUtil {
    public static final Blocks RSBLOCKS = Blocks.INSTANCE;
    public static final Items RSITEMS = Items.INSTANCE;

    private GameTestUtil() {
    }

    @Nullable
    private static Network getNetwork(final GameTestHelper helper, final BlockPos pos) {
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

    public static Runnable networkIsAvailable(final GameTestHelper helper,
                                              final BlockPos networkPos,
                                              final Consumer<Network> networkConsumer) {
        return () -> {
            final Network network = getNetwork(helper, networkPos);
            helper.assertTrue(network != null, "Network is not available");
            networkConsumer.accept(network);
        };
    }

    public static void insert(final GameTestHelper helper,
                              final Network network,
                              final Item resource,
                              final long amount) {
        insert(helper, network, new ItemResource(resource), amount);
    }

    public static void insert(final GameTestHelper helper,
                              final Network network,
                              final Fluid resource,
                              final long amount) {
        insert(helper, network, new FluidResource(resource), amount);
    }

    public static void insert(final GameTestHelper helper,
                              final Network network,
                              final ResourceKey resource,
                              final long amount) {
        final StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        final long inserted = storage.insert(resource, amount, Action.EXECUTE, EmptyActor.INSTANCE);
        helper.assertTrue(inserted == amount, "Resource couldn't be inserted");
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> T requireBlockEntity(
        final GameTestHelper helper,
        final BlockPos pos,
        final Class<T> clazz
    ) {
        final BlockEntity blockEntity = helper.getBlockEntity(pos);
        if (!clazz.isInstance(blockEntity)) {
            throw new GameTestAssertException(
                "Expected block entity of type " + clazz + " but was " + blockEntity.getClass()
            );
        }
        return (T) blockEntity;
    }

    public static void assertFluidPresent(final GameTestHelper helper,
                                          final BlockPos pos,
                                          final Fluid fluid,
                                          final int level) {
        final FluidState fluidState = helper.getLevel().getFluidState(helper.absolutePos(pos));
        helper.assertTrue(
            fluidState.getType() == fluid && fluidState.getAmount() == level,
            "Unexpected " + fluidState.getType() + ", " + fluidState.getAmount()
        );
    }

    public static void assertItemEntityPresentExactly(final GameTestHelper helper,
                                               final ItemStack itemStack,
                                               final BlockPos pos,
                                               final double expansionAmount) {
        final BlockPos blockpos = helper.absolutePos(pos);
        final Iterator<ItemEntity> entityIterator = helper.getLevel().getEntities(EntityType.ITEM,
            (new AABB(blockpos)).inflate(expansionAmount), Entity::isAlive).iterator();

        ItemEntity itemEntity;
        do {
            if (!entityIterator.hasNext()) {
                throw new GameTestAssertException("Expected " + itemStack.getItem().getDescription().getString()
                    + " item at: " + blockpos + " with count: " + itemStack.getCount());
            }

            itemEntity = entityIterator.next();
        } while (!itemEntity.getItem().getItem().equals(itemStack.getItem())
            || itemEntity.getItem().getCount() != itemStack.getCount());
    }

    public static Runnable assertInterfaceEmpty(final GameTestHelper helper,
                                                final BlockPos pos) {
        final var interfaceBlockEntity = requireBlockEntity(helper, pos, InterfaceBlockEntity.class);

        return assertResourceContainerEmpty(interfaceBlockEntity.getDisplayName(),
            interfaceBlockEntity.getExportedResources());
    }

    private static Runnable assertResourceContainerEmpty(final Component displayName,
                                                         final ResourceContainer container) {
        return () -> {
            for (int i = 0; i < container.size(); i++) {
                if (!container.isEmpty(i)) {
                    throw new GameTestAssertException(displayName.getString() + " should be empty");
                }
            }
        };
    }

    public static Runnable interfaceContainsExactly(final GameTestHelper helper,
                                                    final BlockPos pos,
                                                    final ResourceAmount... expected) {
        final var interfaceBlockEntity = requireBlockEntity(helper, pos, InterfaceBlockEntity.class);

        return resourceContainerContainsExactly(helper, interfaceBlockEntity.getDisplayName(),
            interfaceBlockEntity.getExportedResources(), expected);
    }

    private static Runnable resourceContainerContainsExactly(final GameTestHelper helper,
                                                             final Component displayName,
                                                             final ResourceContainer container,
                                                             final ResourceAmount... expected) {
        return () -> {
            // TODO: This does not take duplicate ResourceAmount into account
            for (final ResourceAmount expectedStack : expected) {
                final boolean contains = IntStream.range(0, container.size())
                    .mapToObj(container::get)
                    .anyMatch(resource -> resource != null
                        && resource.getResource().equals(expectedStack.getResource())
                        && resource.getAmount() == expectedStack.getAmount());

                helper.assertTrue(contains, "Expected resource is missing from " + displayName.getString() + ": "
                    + expectedStack + " with count: " + expectedStack.getAmount());
            }

            for (int i = 0; i < container.size(); i++) {
                final ResourceAmount resource = container.get(i);
                if (resource != null) {
                    final boolean wasExpected = Arrays.stream(expected).anyMatch(
                        expectedResource -> expectedResource.getResource().equals(resource.getResource())
                            && expectedResource.getAmount() == resource.getAmount()
                    );

                    helper.assertTrue(wasExpected, "Unexpected resource found in " + displayName.getString() + ": "
                        + resource.getResource() + " with count: " + resource.getAmount());
                }
            }
        };
    }

    public static Runnable containerContainsExactly(final GameTestHelper helper,
                                                    final BlockPos pos,
                                                    final ResourceAmount... expected) {
        final var containerBlockEntity = requireBlockEntity(helper, pos, BaseContainerBlockEntity.class);

        return () -> {
            // TODO: This does not take duplicate ResourceAmount into account
            for (final ResourceAmount expectedStack : expected) {
                final boolean contains = IntStream.range(0, containerBlockEntity.getContainerSize())
                    .mapToObj(containerBlockEntity::getItem)
                    .anyMatch(inContainer -> asResource(inContainer).equals(expectedStack.getResource())
                        && inContainer.getCount() == expectedStack.getAmount());
                helper.assertTrue(contains, "Expected resource is missing from container: "
                    + expectedStack + " with count: " + expectedStack.getAmount());
            }
            for (int i = 0; i < containerBlockEntity.getContainerSize(); i++) {
                final ItemStack inContainer = containerBlockEntity.getItem(i);

                if (!inContainer.isEmpty()) {
                    final boolean wasExpected = Arrays.stream(expected).anyMatch(
                        expectedStack -> expectedStack.getResource().equals(asResource(inContainer))
                            && expectedStack.getAmount() == inContainer.getCount()
                    );
                    helper.assertTrue(wasExpected, "Unexpected resource found in container: "
                        + inContainer.getDescriptionId() + " with count: " + inContainer.getCount());
                }
            }
        };
    }

    public static Runnable storageContainsExactly(final GameTestHelper helper,
                                                  final BlockPos networkPos,
                                                  final ResourceAmount... expected) {
        return networkIsAvailable(helper, networkPos, network -> {
            final StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
            for (final ResourceAmount expectedResource : expected) {
                final boolean contains = storage.getAll()
                    .stream()
                    .anyMatch(inStorage -> inStorage.getResource().equals(expectedResource.getResource())
                        && inStorage.getAmount() == expectedResource.getAmount());
                helper.assertTrue(contains, "Expected resource is missing from storage: " + expectedResource);
            }
            for (final ResourceAmount inStorage : storage.getAll()) {
                final boolean wasExpected = Arrays.stream(expected).anyMatch(
                    expectedResource -> expectedResource.getResource().equals(inStorage.getResource())
                        && expectedResource.getAmount() == inStorage.getAmount()
                );
                helper.assertTrue(wasExpected, "Unexpected resource found in storage: " + inStorage);
            }
        });
    }

    public static ItemResource asResource(final Item item) {
        return new ItemResource(item);
    }

    public static ItemResource asResource(final ItemStack itemStack) {
        return ItemResource.ofItemStack(itemStack);
    }

    public static FluidResource asResource(final Fluid fluid) {
        return new FluidResource(fluid);
    }
}
