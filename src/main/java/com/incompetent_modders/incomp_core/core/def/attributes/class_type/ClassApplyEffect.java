package com.incompetent_modders.incomp_core.core.def.attributes.class_type;

import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttribute;
import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttributeType;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModClassAttributes;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.core.def.attributes.species.ApplyEffectAttribute;
import com.incompetent_modders.incomp_core.core.def.attributes.species.SpeciesTickable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ClassApplyEffect extends ClassAttribute implements ClassTickable {

    public static final Codec<ClassApplyEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(att -> att.effect),
            Codec.INT.fieldOf("duration").forGetter(att -> att.duration),
            Codec.INT.fieldOf("amplifier").forGetter(att -> att.amplifier)
    ).apply(instance, ClassApplyEffect::new));

    private final Holder<MobEffect> effect;
    private final int duration;
    private final int amplifier;

    public ClassApplyEffect(Holder<MobEffect> effect, int duration, int amplifier) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public void tick(Level level, LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(effect, duration, amplifier));
    }

    @Override
    public ClassAttributeType<? extends ClassAttribute> getType() {
        return ModClassAttributes.APPLY_EFFECT.get();
    }
}
