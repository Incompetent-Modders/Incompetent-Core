package com.incompetent_modders.incomp_core.api.json.class_type;

import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.Ability;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.ManaRegenCondition;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public record ClassTypeProperties(boolean canCastSpells, int maxMana, boolean pacifist, boolean useClassSpecificTexture, ManaRegenCondition manaRegenCondition, ClassPassiveEffect passiveEffect, Ability ability, int passiveEffectTickFrequency, int abilityCooldown) {
    public static final Codec<ClassTypeProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("can_cast_spells").forGetter(ClassTypeProperties::canCastSpells),
            Codec.INT.fieldOf("max_mana").forGetter(ClassTypeProperties::maxMana),
            Codec.BOOL.fieldOf("pacifist").forGetter(ClassTypeProperties::pacifist),
            Codec.BOOL.fieldOf("use_class_specific_texture").forGetter(ClassTypeProperties::useClassSpecificTexture),
            ManaRegenCondition.DIRECT_CODEC.fieldOf("mana_regen_condition").forGetter(ClassTypeProperties::manaRegenCondition),
            ClassPassiveEffect.DIRECT_CODEC.fieldOf("passive_effect").forGetter(ClassTypeProperties::passiveEffect),
            Ability.DIRECT_CODEC.fieldOf("ability").forGetter(ClassTypeProperties::ability),
            Codec.INT.fieldOf("passive_effect_tick_frequency").forGetter(ClassTypeProperties::passiveEffectTickFrequency),
            Codec.INT.fieldOf("ability_cooldown").forGetter(ClassTypeProperties::abilityCooldown)
    ).apply(instance, ClassTypeProperties::new));
    
    public boolean canRegenerateMana(Player player, Level level) {
        return manaRegenCondition().apply(level, player);
    }
    
    public void tickClassFeatures(PlayerTickEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide) {
            return;
        }
        if (passiveEffectTickFrequency() == 0) {
            return;
        }
        // Only call the method if the tickFrequency is less than or equal to 0 and then reset the tickFrequency to the passiveEffectTickFrequency
        if (player.tickCount % passiveEffectTickFrequency() == 0) {
            passiveEffect().apply(level, (ServerPlayer) player);
        }
    }
}
