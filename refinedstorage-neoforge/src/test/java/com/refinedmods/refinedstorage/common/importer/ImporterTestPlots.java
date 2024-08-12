package com.refinedmods.refinedstorage.common.importer;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.iface.ExportedResourcesContainer;
import com.refinedmods.refinedstorage.common.iface.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.common.storage.FluidStorageVariant;
import com.refinedmods.refinedstorage.common.storage.ItemStorageVariant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.apache.commons.lang3.function.TriConsumer;

import static com.refinedmods.refinedstorage.common.GameTestUtil.RSBLOCKS;
import static com.refinedmods.refinedstorage.common.GameTestUtil.requireBlockEntity;
import static net.minecraft.core.BlockPos.ZERO;

final class ImporterTestPlots {
    private ImporterTestPlots() {
    }

    static void preparePlot(final GameTestHelper helper,
                            final Direction direction,
                            final TriConsumer<AbstractImporterBlockEntity, BlockPos, GameTestSequence> consumer) {
        helper.setBlock(ZERO.above(), RSBLOCKS.getCreativeController().getDefault());
        helper.setBlock(ZERO.above().above(), RSBLOCKS.getItemStorageBlock(ItemStorageVariant.ONE_K));
        helper.setBlock(
            ZERO.above().above().north(),
            RSBLOCKS.getFluidStorageBlock(FluidStorageVariant.SIXTY_FOUR_B)
        );
        final BlockPos importerPos = ZERO.above().above().above();
        helper.setBlock(importerPos, RSBLOCKS.getImporter().getDefault().rotated(direction));
        consumer.accept(
            requireBlockEntity(helper, importerPos, AbstractImporterBlockEntity.class),
            importerPos,
            helper.startSequence()
        );
    }

    static void prepareChest(final GameTestHelper helper,
                             final BlockPos pos,
                             final ItemStack... stacks) {
        helper.setBlock(pos, Blocks.CHEST.defaultBlockState());
        final var chestBlockEntity = requireBlockEntity(helper, pos, BaseContainerBlockEntity.class);
        for (int i = 0; i < stacks.length; i++) {
            chestBlockEntity.setItem(i, stacks[i]);
        }
    }

    static void prepareInterface(final GameTestHelper helper,
                                 final BlockPos pos,
                                 final ResourceAmount... resource) {
        helper.setBlock(pos, RSBLOCKS.getInterface());
        final var interfaceBlockEntity = requireBlockEntity(helper, pos, InterfaceBlockEntity.class);
        final ExportedResourcesContainer exportedResources = interfaceBlockEntity.getExportedResources();

        for (int i = 0; i < resource.length; i++) {
            exportedResources.set(i, resource[i]);
        }
    }
}
