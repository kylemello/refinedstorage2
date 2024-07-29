package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.list.ResourceList;
import com.refinedmods.refinedstorage.api.resource.list.ResourceListImpl;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ProcessingPatternState(
    List<Optional<ResourceAmount>> inputs,
    List<Optional<ResourceAmount>> outputs
) {
    public static final Codec<ProcessingPatternState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.list(ResourceCodecs.AMOUNT_OPTIONAL_CODEC).fieldOf("inputs").forGetter(ProcessingPatternState::inputs),
        Codec.list(ResourceCodecs.AMOUNT_OPTIONAL_CODEC).fieldOf("outputs").forGetter(ProcessingPatternState::outputs)
    ).apply(instance, ProcessingPatternState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProcessingPatternState> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ResourceCodecs.AMOUNT_STREAM_OPTIONAL_CODEC),
            ProcessingPatternState::inputs,
            ByteBufCodecs.collection(ArrayList::new, ResourceCodecs.AMOUNT_STREAM_OPTIONAL_CODEC),
            ProcessingPatternState::outputs,
            ProcessingPatternState::new
        );

    List<ResourceAmount> getFlatInputs() {
        final ResourceList list = ResourceListImpl.orderPreserving();
        inputs.forEach(input -> input.ifPresent(list::add));
        return new ArrayList<>(list.getAll());
    }

    List<ResourceAmount> getFlatOutputs() {
        final ResourceList list = ResourceListImpl.orderPreserving();
        outputs.forEach(output -> output.ifPresent(list::add));
        return new ArrayList<>(list.getAll());
    }
}
