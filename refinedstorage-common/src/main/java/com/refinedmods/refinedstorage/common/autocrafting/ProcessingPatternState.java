package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceList;
import com.refinedmods.refinedstorage.api.resource.list.MutableResourceListImpl;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ProcessingPatternState(
    List<Optional<Input>> inputs,
    List<Optional<ResourceAmount>> outputs
) {
    public static final Codec<ProcessingPatternState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.list(Input.OPTIONAL_CODEC).fieldOf("inputs").forGetter(ProcessingPatternState::inputs),
        Codec.list(ResourceCodecs.AMOUNT_OPTIONAL_CODEC).fieldOf("outputs").forGetter(ProcessingPatternState::outputs)
    ).apply(instance, ProcessingPatternState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProcessingPatternState> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, Input.OPTIONAL_STREAM_CODEC),
            ProcessingPatternState::inputs,
            ByteBufCodecs.collection(ArrayList::new, ResourceCodecs.AMOUNT_STREAM_OPTIONAL_CODEC),
            ProcessingPatternState::outputs,
            ProcessingPatternState::new
        );

    List<ResourceAmount> getFlatInputs() {
        final MutableResourceList list = MutableResourceListImpl.orderPreserving();
        inputs.forEach(input -> input.map(Input::input).ifPresent(list::add));
        return new ArrayList<>(list.copyState());
    }

    List<ResourceAmount> getFlatOutputs() {
        final MutableResourceList list = MutableResourceListImpl.orderPreserving();
        outputs.forEach(output -> output.ifPresent(list::add));
        return new ArrayList<>(list.copyState());
    }

    public record Input(ResourceAmount input, List<ResourceLocation> allowedAlternativeIds) {
        public static final Codec<Input> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceCodecs.AMOUNT_CODEC.fieldOf("input").forGetter(Input::input),
            Codec.list(ResourceLocation.CODEC).fieldOf("allowedAlternativeIds").forGetter(Input::allowedAlternativeIds)
        ).apply(instance, Input::new));
        public static final Codec<Optional<Input>> OPTIONAL_CODEC = CODEC.optionalFieldOf("input").codec();

        public static final StreamCodec<RegistryFriendlyByteBuf, Input> STREAM_CODEC = StreamCodec.composite(
            ResourceCodecs.AMOUNT_STREAM_CODEC,
            Input::input,
            ByteBufCodecs.collection(ArrayList::new, ResourceLocation.STREAM_CODEC),
            Input::allowedAlternativeIds,
            Input::new
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Input>> OPTIONAL_STREAM_CODEC = ByteBufCodecs
            .optional(STREAM_CODEC);
    }
}
