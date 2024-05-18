package com.incompetent_modders.incomp_core.api.json.species;

import com.incompetent_modders.incomp_core.api.json.species.diet.EnchantmentWeaknessProperties;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviour;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

public record SpeciesProperties(SpeciesBehaviour behaviour, boolean invertHealAndHarm, ResourceLocation dietType, boolean keepOnDeath, List<EnchantmentWeaknessProperties> enchantWeaknesses) {
    public static final Codec<SpeciesProperties> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                SpeciesBehaviour.DIRECT_CODEC.fieldOf("behaviour").forGetter(SpeciesProperties::behaviour),
                Codec.BOOL.optionalFieldOf("invert_heal_and_harm", false).forGetter(SpeciesProperties::invertHealAndHarm),
                ResourceLocation.CODEC.optionalFieldOf("diet_type", CommonUtils.defaultDiet).forGetter(SpeciesProperties::dietType),
                Codec.BOOL.optionalFieldOf("keep_on_death", true).forGetter(SpeciesProperties::keepOnDeath),
                EnchantmentWeaknessProperties.CODEC.listOf().optionalFieldOf("enchant_weaknesses", NonNullList.create()).forGetter(SpeciesProperties::enchantWeaknesses)
        ).apply(instance, SpeciesProperties::new);
    });
    
    public List<EnchantmentWeaknessProperties> enchantWeaknesses() {
        return enchantWeaknesses;
    }
    public boolean hasEnchantWeaknesses() {
        return !enchantWeaknesses.isEmpty();
    }
    
    public void tickSpeciesAttributes(Player player) {
        ResourceLocation speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
        SpeciesAttributes speciesAttributes = SpeciesAttributesListener.getSpeciesTypeAttributes(speciesType);
        SpeciesProperties speciesProperties = SpeciesListener.getSpeciesTypeProperties(speciesType);
        AttributeInstance maxHealthInst = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attackDamageInst = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance attackKnockbackInst = player.getAttribute(Attributes.ATTACK_KNOCKBACK);
        AttributeInstance movementSpeedInst = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance armorInst = player.getAttribute(Attributes.ARMOR);
        AttributeInstance luckInst = player.getAttribute(Attributes.LUCK);
        AttributeInstance blockInteractionRangeInst = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityInteractionRangeInst = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
        AttributeInstance jumpStrength = player.getAttribute(Attributes.JUMP_STRENGTH);
        AttributeInstance knockbackResistance = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        AttributeInstance safeFallDistance = player.getAttribute(Attributes.SAFE_FALL_DISTANCE);
        AttributeInstance scale = player.getAttribute(Attributes.SCALE);
        AttributeInstance stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);
        AttributeInstance armourToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (maxHealthInst == null || attackDamageInst == null || attackKnockbackInst == null || movementSpeedInst == null || armorInst == null || luckInst == null || blockInteractionRangeInst == null || entityInteractionRangeInst == null || gravity == null || jumpStrength == null || knockbackResistance == null || safeFallDistance == null || scale == null || stepHeight == null || armourToughness == null) {
            return;
        }
        maxHealthInst.setBaseValue(speciesAttributes.maxHealth());
        attackDamageInst.setBaseValue(speciesAttributes.attackDamage());
        attackKnockbackInst.setBaseValue(speciesAttributes.attackKnockback());
        movementSpeedInst.setBaseValue(speciesAttributes.moveSpeed());
        armorInst.setBaseValue(speciesAttributes.armour());
        luckInst.setBaseValue(speciesAttributes.luck());
        blockInteractionRangeInst.setBaseValue(speciesAttributes.blockInteractionRange());
        entityInteractionRangeInst.setBaseValue(speciesAttributes.entityInteractionRange());
        gravity.setBaseValue(speciesAttributes.gravity());
        jumpStrength.setBaseValue(speciesAttributes.jumpStrength());
        knockbackResistance.setBaseValue(speciesAttributes.knockbackResistance());
        safeFallDistance.setBaseValue(speciesAttributes.safeFallDistance());
        scale.setBaseValue(speciesAttributes.scale());
        stepHeight.setBaseValue(speciesAttributes.stepHeight());
        armourToughness.setBaseValue(speciesAttributes.armourToughness());
        SpeciesBehaviour behaviour = speciesProperties.behaviour();
        behaviour.apply(player.level(), player);
    }
}
