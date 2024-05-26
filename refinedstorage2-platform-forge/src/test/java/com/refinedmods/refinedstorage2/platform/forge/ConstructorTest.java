package com.refinedmods.refinedstorage2.platform.forge;

import com.refinedmods.refinedstorage2.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage2.platform.common.Platform;
import com.refinedmods.refinedstorage2.platform.common.constructordestructor.ConstructorBlockEntity;
import com.refinedmods.refinedstorage2.platform.common.storage.FluidStorageType;
import com.refinedmods.refinedstorage2.platform.common.storage.ItemStorageType;
import com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.apache.commons.lang3.function.TriConsumer;

import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.DIRT;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.RSBLOCKS;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.STONE;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.WATER;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.assertFluidPresent;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.insert;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.networkIsAvailable;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.requireBlockEntity;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.storageContainsExactly;
import static net.minecraft.core.BlockPos.ZERO;

@GameTestHolder(IdentifierUtil.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ConstructorTest {

    private ConstructorTest() {
    }

    private static void prepareConstructorPlot(final GameTestHelper helper,
                                               final Direction direction,
                                               final TriConsumer<ConstructorBlockEntity, BlockPos, GameTestSequence>
                                                   consumer) {
        helper.setBlock(ZERO.above(), RSBLOCKS.getCreativeController().getDefault());
        helper.setBlock(ZERO.above().above(), RSBLOCKS.getItemStorageBlock(ItemStorageType.Variant.ONE_K));
        helper.setBlock(
            ZERO.above().above().north(),
            RSBLOCKS.getFluidStorageBlock(FluidStorageType.Variant.SIXTY_FOUR_B)
        );
        final BlockPos constructorPos = ZERO.above().above().above();
        helper.setBlock(constructorPos, RSBLOCKS.getConstructor().getDefault().rotated(direction));
        consumer.accept(
            requireBlockEntity(helper, constructorPos, ConstructorBlockEntity.class),
            constructorPos,
            helper.startSequence()
        );
    }

    @GameTest(template = "empty_15x15")
    public static void shouldPlaceBlock(final GameTestHelper helper) {
        prepareConstructorPlot(helper, Direction.EAST, (constructor, pos, sequence) -> {
            // Arrange
            sequence.thenWaitUntil(networkIsAvailable(helper, pos, network -> {
                insert(helper, network, DIRT, 10);
                insert(helper, network, STONE, 15);
            }));

            // Act
            constructor.setFilters(List.of(DIRT));

            // Assert
            sequence
                .thenWaitUntil(() -> helper.assertBlockPresent(Blocks.DIRT, pos.east()))
                .thenWaitUntil(storageContainsExactly(
                    helper,
                    pos,
                    new ResourceAmount(DIRT, 9),
                    new ResourceAmount(STONE, 15)
                ))
                .thenSucceed();
        });
    }

    @GameTest(template = "empty_15x15")
    public static void shouldPlaceWater(final GameTestHelper helper) {
        prepareConstructorPlot(helper, Direction.EAST, (constructor, pos, sequence) -> {
            // Arrange
            sequence.thenWaitUntil(networkIsAvailable(helper, pos, network -> {
                insert(helper, network, DIRT, 10);
                insert(helper, network, STONE, 15);
                insert(helper, network, WATER, Platform.INSTANCE.getBucketAmount() * 2);
            }));

            // Act
            constructor.setFilters(List.of(WATER));

            // Assert
            sequence
                .thenWaitUntil(() -> assertFluidPresent(helper, pos.east(), Fluids.WATER, FluidState.AMOUNT_FULL))
                .thenWaitUntil(storageContainsExactly(
                    helper,
                    pos,
                    new ResourceAmount(DIRT, 10),
                    new ResourceAmount(STONE, 15),
                    new ResourceAmount(WATER, Platform.INSTANCE.getBucketAmount())
                ))
                .thenSucceed();
        });
    }

    @GameTest(template = "empty_15x15")
    public static void shouldDropItem(final GameTestHelper helper) {
        prepareConstructorPlot(helper, Direction.EAST, (constructor, pos, sequence) -> {
            // Arrange
            sequence.thenWaitUntil(networkIsAvailable(helper, pos, network -> {
                insert(helper, network, DIRT, 10);
                insert(helper, network, STONE, 15);
            }));

            // Act
            constructor.setDropItems(true);
            constructor.setFilters(List.of(DIRT));

            // Assert
            sequence
                .thenWaitUntil(() -> helper.assertBlockNotPresent(Blocks.DIRT, pos.east()))
                .thenWaitUntil(() -> helper.assertItemEntityPresent(Items.DIRT, pos.east(), 1))
                .thenWaitUntil(storageContainsExactly(
                    helper,
                    pos,
                    new ResourceAmount(DIRT, 9),
                    new ResourceAmount(STONE, 15)
                ))
                .thenSucceed();
        });
    }
}
