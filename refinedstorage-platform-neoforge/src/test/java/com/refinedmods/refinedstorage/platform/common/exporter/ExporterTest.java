package com.refinedmods.refinedstorage.platform.common.exporter;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.platform.common.Platform;
import com.refinedmods.refinedstorage.platform.common.util.IdentifierUtil;

import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static com.refinedmods.refinedstorage.platform.common.GameTestUtil.RSBLOCKS;
import static com.refinedmods.refinedstorage.platform.common.GameTestUtil.RSITEMS;
import static com.refinedmods.refinedstorage.platform.common.GameTestUtil.asResource;
import static com.refinedmods.refinedstorage.platform.common.GameTestUtil.containerContainsExactly;
import static com.refinedmods.refinedstorage.platform.common.GameTestUtil.insert;
import static com.refinedmods.refinedstorage.platform.common.GameTestUtil.interfaceContainsExactly;
import static com.refinedmods.refinedstorage.platform.common.GameTestUtil.networkIsAvailable;
import static com.refinedmods.refinedstorage.platform.common.GameTestUtil.storageContainsExactly;
import static com.refinedmods.refinedstorage.platform.common.exporter.ExporterTestPlots.preparePlot;
import static net.minecraft.world.item.Items.DIAMOND_CHESTPLATE;
import static net.minecraft.world.item.Items.DIRT;
import static net.minecraft.world.item.Items.STONE;
import static net.minecraft.world.level.material.Fluids.WATER;

@GameTestHolder(IdentifierUtil.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ExporterTest {
    private ExporterTest() {
    }

    @GameTest(template = "empty_15x15")
    public static void shouldExportItem(final GameTestHelper helper) {
        preparePlot(helper, Blocks.CHEST, Direction.EAST, (exporter, pos, sequence) -> {
            // Arrange
            final ItemStack damagedDiamondChestplate = DIAMOND_CHESTPLATE.getDefaultInstance();
            damagedDiamondChestplate.setDamageValue(500);

            sequence.thenWaitUntil(networkIsAvailable(helper, pos, network -> {
                insert(helper, network, DIRT, 10);
                insert(helper, network, STONE, 15);
                insert(helper, network, asResource(damagedDiamondChestplate), 1);
            }));

            // Act
            exporter.setFilters(List.of(asResource(DIRT), asResource(DIAMOND_CHESTPLATE.getDefaultInstance())));

            // Assert
            sequence
                .thenWaitUntil(containerContainsExactly(
                    helper,
                    pos.east(),
                    new ResourceAmount(asResource(DIRT), 1)
                ))
                .thenWaitUntil(storageContainsExactly(
                    helper,
                    pos,
                    new ResourceAmount(asResource(DIRT), 9),
                    new ResourceAmount(asResource(STONE), 15),
                    new ResourceAmount(asResource(damagedDiamondChestplate), 1)
                ))
                .thenSucceed();
        });
    }

    @GameTest(template = "empty_15x15")
    public static void shouldExportItemWithStackUpgrade(final GameTestHelper helper) {
        preparePlot(helper, Blocks.CHEST, Direction.EAST, (exporter, pos, sequence) -> {
            // Arrange
            sequence.thenWaitUntil(networkIsAvailable(helper, pos, network -> {
                insert(helper, network, DIRT, 65);
                insert(helper, network, STONE, 1);
            }));

            // Act
            exporter.setFilters(List.of(asResource(DIRT)));
            exporter.addUpgradeItem(RSITEMS.getStackUpgrade());

            // Assert
            sequence
                .thenIdle(9)
                .thenExecute(containerContainsExactly(
                    helper,
                    pos.east(),
                    new ResourceAmount(asResource(DIRT), 64)
                ))
                .thenExecute(storageContainsExactly(
                    helper,
                    pos,
                    new ResourceAmount(asResource(DIRT), 1),
                    new ResourceAmount(asResource(STONE), 1)
                ))
                .thenExecute(() -> exporter.setFilters(List.of(asResource(STONE))))
                .thenIdle(9)
                .thenExecute(containerContainsExactly(
                    helper,
                    pos.east(),
                    new ResourceAmount(asResource(DIRT), 64),
                    new ResourceAmount(asResource(STONE), 1)
                ))
                .thenExecute(storageContainsExactly(
                    helper,
                    pos,
                    new ResourceAmount(asResource(DIRT), 1)
                ))
                .thenSucceed();
        });
    }

    @GameTest(template = "empty_15x15")
    public static void shouldExportItemFuzzy(final GameTestHelper helper) {
        preparePlot(helper, Blocks.CHEST, Direction.EAST, (exporter, pos, sequence) -> {
            // Arrange
            final ItemStack damagedDiamondChestplate = DIAMOND_CHESTPLATE.getDefaultInstance();
            damagedDiamondChestplate.setDamageValue(500);

            sequence.thenWaitUntil(networkIsAvailable(helper, pos, network -> {
                insert(helper, network, DIRT, 10);
                insert(helper, network, STONE, 15);
                insert(helper, network, DIAMOND_CHESTPLATE, 1);
                insert(helper, network, asResource(damagedDiamondChestplate), 1);
            }));

            // Act
            exporter.setFuzzyMode(true);
            exporter.setFilters(List.of(asResource(DIAMOND_CHESTPLATE)));

            // Assert
            sequence
                .thenWaitUntil(containerContainsExactly(
                    helper,
                    pos.east(),
                    new ResourceAmount(asResource(DIAMOND_CHESTPLATE.getDefaultInstance()), 1),
                    new ResourceAmount(asResource(damagedDiamondChestplate), 1)
                ))
                .thenWaitUntil(storageContainsExactly(
                    helper,
                    pos,
                    new ResourceAmount(asResource(DIRT), 10),
                    new ResourceAmount(asResource(STONE), 15)
                ))
                .thenSucceed();
        });
    }

    @GameTest(template = "empty_15x15")
    public static void shouldExportFluid(final GameTestHelper helper) {
        preparePlot(helper, Blocks.CAULDRON, Direction.EAST, (exporter, pos, sequence) -> {
            // Arrange
            sequence.thenWaitUntil(networkIsAvailable(helper, pos, network -> {
                insert(helper, network, DIRT, 10);
                insert(helper, network, STONE, 15);
                insert(helper, network, WATER, Platform.INSTANCE.getBucketAmount() * 2);
            }));

            // Act
            exporter.setFilters(List.of(asResource(WATER)));

            // Assert
            sequence
                .thenWaitUntil(() -> helper.assertBlockProperty(pos.east(), LayeredCauldronBlock.LEVEL,
                    LayeredCauldronBlock.MAX_FILL_LEVEL))
                .thenWaitUntil(storageContainsExactly(
                    helper,
                    pos,
                    new ResourceAmount(asResource(DIRT), 10),
                    new ResourceAmount(asResource(STONE), 15),
                    new ResourceAmount(asResource(WATER), Platform.INSTANCE.getBucketAmount())
                ))
                .thenSucceed();
        });
    }

    @GameTest(template = "empty_15x15")
    public static void shouldExportFluidWithStackUpgrade(final GameTestHelper helper) {
        preparePlot(helper, RSBLOCKS.getInterface(), Direction.EAST, (exporter, pos, sequence) -> {
            // Arrange
            sequence.thenWaitUntil(networkIsAvailable(helper, pos, network -> {
                insert(helper, network, DIRT, 10);
                insert(helper, network, STONE, 15);
                insert(helper, network, WATER, Platform.INSTANCE.getBucketAmount() * 64);
            }));

            // Act
            exporter.addUpgradeItem(RSITEMS.getStackUpgrade());
            exporter.setFilters(List.of(asResource(WATER)));

            // Assert
            sequence
                .thenIdle(9)
                .thenExecute(storageContainsExactly(
                    helper,
                    pos,
                    new ResourceAmount(asResource(DIRT), 10),
                    new ResourceAmount(asResource(STONE), 15)
                ))
                .thenExecute(interfaceContainsExactly(
                    helper,
                    pos.east(),
                    new ResourceAmount(asResource(WATER), Platform.INSTANCE.getBucketAmount() * 16),
                    new ResourceAmount(asResource(WATER), Platform.INSTANCE.getBucketAmount() * 16),
                    new ResourceAmount(asResource(WATER), Platform.INSTANCE.getBucketAmount() * 16),
                    new ResourceAmount(asResource(WATER), Platform.INSTANCE.getBucketAmount() * 16)
                ))

                .thenSucceed();
        });
    }
}
