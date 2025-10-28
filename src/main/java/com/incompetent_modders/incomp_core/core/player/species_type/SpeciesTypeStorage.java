package com.incompetent_modders.incomp_core.core.player.species_type;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesTypes;
import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.incompetent_modders.incomp_core.core.player.AbilityCooldownData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public record SpeciesTypeStorage(ResourceKey<SpeciesType> speciesType, AbilityCooldownData cooldownData) {
    public static final SpeciesTypeStorage DEFAULT = new SpeciesTypeStorage(ModSpeciesTypes.HUMAN, AbilityCooldownData.EMPTY_DATA);

    public static final Codec<SpeciesTypeStorage> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(ModRegistries.Keys.SPECIES_TYPE).fieldOf("species_type").forGetter(SpeciesTypeStorage::speciesType),
            AbilityCooldownData.CODEC.optionalFieldOf("cooldown_data", AbilityCooldownData.EMPTY_DATA).forGetter(SpeciesTypeStorage::cooldownData)
    ).apply(instance, SpeciesTypeStorage::new));

    public static final Codec<SpeciesTypeStorage> CODEC = Codec.withAlternative(FULL_CODEC,
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceKey.codec(ModRegistries.Keys.SPECIES_TYPE).fieldOf("species_type").forGetter(SpeciesTypeStorage::speciesType),
                    AbilityCooldownData.CODEC.optionalFieldOf("cooldown_data", AbilityCooldownData.EMPTY_DATA).forGetter(SpeciesTypeStorage::cooldownData)
            ).apply(instance, SpeciesTypeStorage::new))
    );

    public static final StreamCodec<FriendlyByteBuf, SpeciesTypeStorage> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(ModRegistries.Keys.SPECIES_TYPE),
            SpeciesTypeStorage::speciesType,
            AbilityCooldownData.STREAM_CODEC,
            SpeciesTypeStorage::cooldownData,
            SpeciesTypeStorage::new
    );

    public static Codec<SpeciesTypeStorage> codec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                ResourceKey.codec(ModRegistries.Keys.SPECIES_TYPE).fieldOf("species_type").forGetter(storage -> storage.speciesType),
                AbilityCooldownData.CODEC.optionalFieldOf("cooldown_data", AbilityCooldownData.EMPTY_DATA).forGetter(storage -> storage.cooldownData)
        ).apply(instance, SpeciesTypeStorage::new));
    }

    public static SpeciesTypeStorage create(ResourceKey<SpeciesType> speciesType, AbilityCooldownData cooldownData) {
        return new SpeciesTypeStorage(speciesType, cooldownData);
    }

    public static SpeciesTypeStorage createDefault() {
        return new SpeciesTypeStorage(ModSpeciesTypes.HUMAN, AbilityCooldownData.EMPTY_DATA);
    }


}
