package com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.content.ContentNames;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class AutocrafterManagerBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<SimpleNetworkNode> {
    public AutocrafterManagerBlockEntity(final BlockPos pos, final BlockState state) {
        super(BlockEntities.INSTANCE.getAutocrafterManager(), pos, state, new SimpleNetworkNode(
            Platform.INSTANCE.getConfig().getAutocrafterManager().getEnergyUsage()
        ));
    }

    @Override
    public Component getName() {
        return ContentNames.AUTOCRAFTER_MANAGER;
    }

    @Override
    protected boolean doesBlockStateChangeWarrantNetworkNodeUpdate(final BlockState oldBlockState,
                                                                   final BlockState newBlockState) {
        return AbstractDirectionalBlock.didDirectionChange(oldBlockState, newBlockState);
    }
}
