package com.incompetent_modders.incomp_core.core.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record AbilityCooldownData(int abilityCooldown) {
    public static final AbilityCooldownData EMPTY_DATA = new AbilityCooldownData(-1);

    public static final MapCodec<AbilityCooldownData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("ability_cooldown").forGetter(AbilityCooldownData::abilityCooldown)
    ).apply(instance, AbilityCooldownData::new));

    public static final Codec<AbilityCooldownData> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<FriendlyByteBuf, AbilityCooldownData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            AbilityCooldownData::abilityCooldown,
            AbilityCooldownData::new
    );

    public boolean hasAbilityCooldown() {
        return abilityCooldown != -1;
    }

    public static AbilityCooldownData of(int abilityCooldown) {
        return new AbilityCooldownData(abilityCooldown);
    }
}
