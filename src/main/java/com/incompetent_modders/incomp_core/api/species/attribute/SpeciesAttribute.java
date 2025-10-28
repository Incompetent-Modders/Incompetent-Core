package com.incompetent_modders.incomp_core.api.species.attribute;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Map;

public abstract class SpeciesAttribute {

    public static final Codec<Map<SpeciesAttributeType<?>, SpeciesAttribute>> MAPPED_CODEC = Codec.dispatchedMap(
            ModRegistries.SPECIES_ATTRIBUTE_TYPE.byNameCodec(), SpeciesAttributeType::codec);

    public abstract SpeciesAttributeType<? extends SpeciesAttribute> getType();
}
