package com.incompetent_modders.incomp_core.api.json.potion;

import com.incompetent_modders.incomp_core.api.json.potion.convert.Convert;
import com.incompetent_modders.incomp_core.api.player.ClassData;
import com.incompetent_modders.incomp_core.api.player.SpeciesData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record PotionEffectProperties(float manaCostModifier, float manaRegenModifier, Convert convertToSpeciesOnFinish, Convert convertToClassOnFinish) {
    public static final Codec<PotionEffectProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("mana_cost_modifier", 1.0F).forGetter(PotionEffectProperties::manaCostModifier),
            Codec.FLOAT.optionalFieldOf("mana_regen_modifier", 1.0F).forGetter(PotionEffectProperties::manaRegenModifier),
            Convert.CODEC.optionalFieldOf("species_convert", Convert.EMPTY_CONVERT).forGetter(PotionEffectProperties::convertToSpeciesOnFinish),
            Convert.CODEC.optionalFieldOf("class_convert", Convert.EMPTY_CONVERT).forGetter(PotionEffectProperties::convertToClassOnFinish)
    ).apply(instance, PotionEffectProperties::new));
    
    public boolean hasSpeciesConvert() {
        return !this.convertToSpeciesOnFinish().equals(Convert.EMPTY_CONVERT);
    }
    
    public boolean hasClassConvert() {
        return !this.convertToClassOnFinish().equals(Convert.EMPTY_CONVERT);
    }
    
    public void applySpeciesConvert(Player player) {
        if (this.hasSpeciesConvert() && convertToSpeciesOnFinish.condition().shouldConvert(player)) {
            SpeciesData.Set.playerSpecies(player, this.convertToSpeciesOnFinish().convertTo());
        }
    }
    
    public void applyClassConvert(Player player) {
        if (this.hasClassConvert() && convertToClassOnFinish.condition().shouldConvert(player)) {
            ClassData.Set.playerClassType(player, this.convertToClassOnFinish().convertTo());
        }
    }
}
