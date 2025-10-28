package com.incompetent_modders.incomp_core.core.def.attributes.species;

import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ApplyEffectAttribute extends SpeciesAttribute implements SpeciesTickable {

    public static final Codec<ApplyEffectAttribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(att -> att.effect),
            Codec.INT.fieldOf("duration").forGetter(att -> att.duration),
            Codec.INT.fieldOf("amplifier").forGetter(att -> att.amplifier)
    ).apply(instance, ApplyEffectAttribute::new));

    private final Holder<MobEffect> effect;
    private final int duration;
    private final int amplifier;

    public ApplyEffectAttribute(Holder<MobEffect> effect, int duration, int amplifier) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public void tick(Level level, LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(effect, duration, amplifier));
    }

    @Override
    public SpeciesAttributeType<? extends SpeciesAttribute> getType() {
        return ModSpeciesAttributes.APPLY_EFFECT.get();
    }
}
