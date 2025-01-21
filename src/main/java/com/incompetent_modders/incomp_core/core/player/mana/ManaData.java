package com.incompetent_modders.incomp_core.core.player.mana;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record ManaData(double amount, int limit) {
    public static final ManaData EMPTY = new ManaData(0, 0);

    public static final MapCodec<ManaData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("amount").forGetter(ManaData::amount),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("limit").forGetter(ManaData::limit)
    ).apply(instance, ManaData::new));

    public static final Codec<ManaData> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<FriendlyByteBuf, ManaData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            ManaData::amount,
            ByteBufCodecs.INT,
            ManaData::limit,
            ManaData::new
    );

    public static ManaData of(double amount, int limit) {
        return new ManaData(amount, limit);
    }

    public static ManaData createEmpty() {
        return ManaData.of(0, 0);
    }

    public ManaData combine(ManaData data) {
        return new ManaData(this.amount + data.amount, this.limit + data.limit);
    }
}
