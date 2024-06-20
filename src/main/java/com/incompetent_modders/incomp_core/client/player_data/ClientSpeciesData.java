package com.incompetent_modders.incomp_core.client.player_data;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviourType;
import net.minecraft.resources.ResourceLocation;

public class ClientSpeciesData {
    private static ClientSpeciesData instance;
    
    private double speciesMaxHealth;
    private double speciesAttackDamage;
    private double speciesAttackKnockback;
    private double speciesMovementSpeed;
    private double speciesArmor;
    private double speciesLuck;
    private double speciesBlockInteractionRange;
    private double speciesEntityInteractionRange;
    private double speciesGravity;
    private double speciesJumpStrength;
    private double speciesKnockbackResistance;
    private double speciesSafeFallDistance;
    private double speciesScale;
    private double speciesStepHeight;
    private double speciesArmourToughness;
    private ResourceLocation species;
    private boolean speciesInvertedHealAndHarm;
    private boolean keepSpeciesOnDeath;
    private ResourceLocation speciesDiet;
    private SpeciesBehaviourType<?> speciesBehaviour;
    private AbilityType<?> speciesAbility;
    private int speciesAbilityCooldown;
    
    private ClientSpeciesData() {
        IncompCore.LOGGER.info("[CLIENT ONLY] Initialized client species data");
    }
    
    public static ClientSpeciesData getInstance() {
        if (instance == null) {
            instance = new ClientSpeciesData();
        }
        return instance;
    }
    
    public double getSpeciesMaxHealth() {
        return speciesMaxHealth;
    }
    
    public void setSpeciesMaxHealth(double speciesMaxHealth) {
        this.speciesMaxHealth = speciesMaxHealth;
    }
    
    public double getSpeciesAttackDamage() {
        return speciesAttackDamage;
    }
    
    public void setSpeciesAttackDamage(double speciesAttackDamage) {
        this.speciesAttackDamage = speciesAttackDamage;
    }
    
    public double getSpeciesAttackKnockback() {
        return speciesAttackKnockback;
    }
    
    public void setSpeciesAttackKnockback(double speciesAttackKnockback) {
        this.speciesAttackKnockback = speciesAttackKnockback;
    }
    
    public double getSpeciesMovementSpeed() {
        return speciesMovementSpeed;
    }
    
    public void setSpeciesMovementSpeed(double speciesMovementSpeed) {
        this.speciesMovementSpeed = speciesMovementSpeed;
    }
    
    public double getSpeciesArmor() {
        return speciesArmor;
    }
    
    public void setSpeciesArmor(double speciesArmor) {
        this.speciesArmor = speciesArmor;
    }
    
    public double getSpeciesLuck() {
        return speciesLuck;
    }
    
    public void setSpeciesLuck(double speciesLuck) {
        this.speciesLuck = speciesLuck;
    }
    
    public double getSpeciesBlockInteractionRange() {
        return speciesBlockInteractionRange;
    }
    
    public void setSpeciesBlockInteractionRange(double speciesBlockInteractionRange) {
        this.speciesBlockInteractionRange = speciesBlockInteractionRange;
    }
    
    public double getSpeciesEntityInteractionRange() {
        return speciesEntityInteractionRange;
    }
    
    public void setSpeciesEntityInteractionRange(double speciesEntityInteractionRange) {
        this.speciesEntityInteractionRange = speciesEntityInteractionRange;
    }
    
    public double getSpeciesGravity() {
        return speciesGravity;
    }
    
    public void setSpeciesGravity(double speciesGravity) {
        this.speciesGravity = speciesGravity;
    }
    
    public double getSpeciesJumpStrength() {
        return speciesJumpStrength;
    }
    
    public void setSpeciesJumpStrength(double speciesJumpStrength) {
        this.speciesJumpStrength = speciesJumpStrength;
    }
    
    public double getSpeciesKnockbackResistance() {
        return speciesKnockbackResistance;
    }
    
    public void setSpeciesKnockbackResistance(double speciesKnockbackResistance) {
        this.speciesKnockbackResistance = speciesKnockbackResistance;
    }
    
    public double getSpeciesSafeFallDistance() {
        return speciesSafeFallDistance;
    }
    
    public void setSpeciesSafeFallDistance(double speciesSafeFallDistance) {
        this.speciesSafeFallDistance = speciesSafeFallDistance;
    }
    
    public double getSpeciesScale() {
        return speciesScale;
    }
    
    public void setSpeciesScale(double speciesScale) {
        this.speciesScale = speciesScale;
    }
    
    public double getSpeciesStepHeight() {
        return speciesStepHeight;
    }
    
    public void setSpeciesStepHeight(double speciesStepHeight) {
        this.speciesStepHeight = speciesStepHeight;
    }
    
    public double getSpeciesArmourToughness() {
        return speciesArmourToughness;
    }
    
    public void setSpeciesArmourToughness(double speciesArmourToughness) {
        this.speciesArmourToughness = speciesArmourToughness;
    }
    
    public ResourceLocation getSpecies() {
        return species;
    }
    
    public void setSpecies(ResourceLocation species) {
        this.species = species;
    }
    
    public boolean isSpeciesInvertedHealAndHarm() {
        return speciesInvertedHealAndHarm;
    }
    
    public void setSpeciesInvertedHealAndHarm(boolean speciesInvertedHealAndHarm) {
        this.speciesInvertedHealAndHarm = speciesInvertedHealAndHarm;
    }
    
    public boolean isKeepSpeciesOnDeath() {
        return keepSpeciesOnDeath;
    }
    
    public void setKeepSpeciesOnDeath(boolean keepSpeciesOnDeath) {
        this.keepSpeciesOnDeath = keepSpeciesOnDeath;
    }
    
    public ResourceLocation getSpeciesDiet() {
        return speciesDiet;
    }
    
    public void setSpeciesDiet(ResourceLocation speciesDiet) {
        this.speciesDiet = speciesDiet;
    }
    
    public SpeciesBehaviourType<?> getSpeciesBehaviour() {
        return speciesBehaviour;
    }
    
    public void setSpeciesBehaviour(SpeciesBehaviourType<?> speciesBehaviour) {
        this.speciesBehaviour = speciesBehaviour;
    }
    
    public AbilityType<?> getSpeciesAbility() {
        return speciesAbility;
    }
    
    public void setSpeciesAbility(AbilityType<?> speciesAbility) {
        this.speciesAbility = speciesAbility;
    }
    
    public int getSpeciesAbilityCooldown() {
        return speciesAbilityCooldown;
    }
    
    public void setSpeciesAbilityCooldown(int speciesAbilityCooldown) {
        this.speciesAbilityCooldown = speciesAbilityCooldown;
    }
}
