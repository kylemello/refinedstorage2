package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.common.content.BlockColorMap;
import com.refinedmods.refinedstorage2.platform.common.content.BlockConstants;
import com.refinedmods.refinedstorage2.platform.common.content.BlockEntities;
import com.refinedmods.refinedstorage2.platform.common.content.Blocks;
import com.refinedmods.refinedstorage2.platform.common.support.AbstractBlockEntityTicker;
import com.refinedmods.refinedstorage2.platform.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage2.platform.common.support.BaseBlockItem;
import com.refinedmods.refinedstorage2.platform.common.support.BlockItemProvider;
import com.refinedmods.refinedstorage2.platform.common.support.ColorableBlock;
import com.refinedmods.refinedstorage2.platform.common.support.NetworkNodeBlockItem;
import com.refinedmods.refinedstorage2.platform.common.support.direction.DirectionType;
import com.refinedmods.refinedstorage2.platform.common.support.direction.HorizontalDirection;
import com.refinedmods.refinedstorage2.platform.common.support.direction.HorizontalDirectionType;
import com.refinedmods.refinedstorage2.platform.common.support.network.NetworkNodeBlockEntityTicker;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

public class SecurityManagerBlock extends AbstractDirectionalBlock<HorizontalDirection>
    implements ColorableBlock<SecurityManagerBlock, BaseBlockItem>, BlockItemProvider<BaseBlockItem>, EntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final MutableComponent HELP = createTranslation("block", "security_manager.help");
    private static final AbstractBlockEntityTicker<SecurityManagerBlockEntity> TICKER =
        new NetworkNodeBlockEntityTicker<>(BlockEntities.INSTANCE::getSecurityManager, ACTIVE);

    private final DyeColor color;
    private final MutableComponent name;

    public SecurityManagerBlock(final DyeColor color, final MutableComponent name) {
        super(BlockConstants.PROPERTIES);
        this.color = color;
        this.name = name;
    }

    @Override
    protected BlockState getDefaultState() {
        return super.getDefaultState().setValue(ACTIVE, false);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Override
    protected DirectionType<HorizontalDirection> getDirectionType() {
        return HorizontalDirectionType.INSTANCE;
    }

    @Override
    public BlockColorMap<SecurityManagerBlock, BaseBlockItem> getBlockColorMap() {
        return Blocks.INSTANCE.getSecurityManager();
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public MutableComponent getName() {
        return name;
    }

    @Override
    public BaseBlockItem createBlockItem() {
        return new NetworkNodeBlockItem(this, HELP);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new SecurityManagerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <O extends BlockEntity> BlockEntityTicker<O> getTicker(final Level level,
                                                                  final BlockState blockState,
                                                                  final BlockEntityType<O> type) {
        return TICKER.get(level, type);
    }

    @Override
    public boolean canAlwaysConnect() {
        return true;
    }
}
