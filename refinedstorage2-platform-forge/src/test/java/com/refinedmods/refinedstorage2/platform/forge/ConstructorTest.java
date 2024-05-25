package com.refinedmods.refinedstorage2.platform.forge;

import com.refinedmods.refinedstorage2.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage2.platform.common.constructordestructor.ConstructorBlockEntity;
import com.refinedmods.refinedstorage2.platform.common.storage.ItemStorageType;
import com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil;

import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.DIRT;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.RSBLOCKS;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.itemIsInserted;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.requireBlockEntity;
import static com.refinedmods.refinedstorage2.platform.forge.GameTestUtil.storageMustContainExactly;
import static net.minecraft.core.BlockPos.ZERO;

@GameTestHolder(IdentifierUtil.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ConstructorTest {
    private ConstructorTest() {
    }

    @GameTest(template = "empty_15x15")
    public static void shouldPlaceBlock(final GameTestHelper helper) {
        // Arrange
        helper.setBlock(ZERO.above(), RSBLOCKS.getCreativeController().getDefault());
        helper.setBlock(ZERO.above().above(), RSBLOCKS.getConstructor().getDefault().rotated(Direction.EAST));
        helper.setBlock(
            ZERO.above().above().above(),
            RSBLOCKS.getItemStorageBlock(ItemStorageType.Variant.ONE_K)
        );

        final var seq = helper.startSequence();
        seq.thenWaitUntil(itemIsInserted(helper, ZERO.above().above(), DIRT, 10));

        // Act
        requireBlockEntity(helper, ZERO.above().above(), ConstructorBlockEntity.class).setFilters(List.of(DIRT));

        // Assert
        seq.thenWaitUntil(() -> helper.assertBlockPresent(Blocks.DIRT, ZERO.above().above().east()))
            .thenWaitUntil(storageMustContainExactly(helper, ZERO.above().above(), new ResourceAmount(DIRT, 9)))
            .thenSucceed();
    }
}
