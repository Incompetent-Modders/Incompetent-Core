package com.incompetent_modders.incomp_core.core.def.abilities;

import com.incompetent_modders.incomp_core.api.class_type.ability.Ability;
import com.incompetent_modders.incomp_core.api.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.common.registry.ModAbilities;
import com.incompetent_modders.incomp_core.common.util.Utils;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ApplyEffectAbility extends Ability {
    public static final MapCodec<ApplyEffectAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(ApplyEffectAbility::getEffect),
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(ApplyEffectAbility::getDuration),
            LevelBasedValue.CODEC.fieldOf("amplifier").forGetter(ApplyEffectAbility::getAmplifier)
    ).apply(instance, ApplyEffectAbility::new));
    
    private final Holder<MobEffect> effect;
    private final LevelBasedValue duration;
    private final LevelBasedValue amplifier;
    
    public ApplyEffectAbility(Holder<MobEffect> effect, LevelBasedValue duration, LevelBasedValue amplifier) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
    }
    
    public Holder<MobEffect> getEffect() {
        return effect;
    }
    
    public LevelBasedValue getDuration() {
        return duration;
    }
    
    public LevelBasedValue getAmplifier() {
        return amplifier;
    }
    
    @Override
    public void apply(int classLevel, Level level, Player player) {
        int duration = Math.round(getDuration().calculate(classLevel));
        int amplifier = Math.round(getAmplifier().calculate(classLevel));
        player.addEffect(new MobEffectInstance(getEffect(), duration, amplifier));
    }

    @Override
    public List<Component> description(Player player) {
        int classLevel = PlayerDataHelper.getClassLevel(player);
        int duration = Math.round(getDuration().calculate(classLevel));
        int amplifier = Math.round(getAmplifier().calculate(classLevel)) + 1;
        List<Component> description = new ArrayList<>();
        description.add(Utils.formatEffect(this.effect, amplifier));
        description.add(Component.translatable("incompetent_core.abilities.duration_seconds", Utils.timeFromTicks(duration, 2)));
        return description;
    }

    @Override
    public AbilityType<? extends Ability> getType() {
        return ModAbilities.APPLY_EFFECT.get();
    }
}
