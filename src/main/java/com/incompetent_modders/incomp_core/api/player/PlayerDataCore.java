package com.incompetent_modders.incomp_core.api.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.json.species.DietType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.ClassAbilityType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffectType;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviourType;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.registry.ModSpeciesTypes;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PlayerDataCore {
    public static final String CLASS_DATA_ID = IncompCore.MODID + ":ClassData";
    public static final String SPECIES_DATA_ID = IncompCore.MODID + ":SpeciesData";
    public static final String MANA_DATA_ID = IncompCore.MODID + ":ManaData";
    
    public static CompoundTag getClassData(Player spe) {
        CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
        if (nc == null)
            return new CompoundTag();
        return nc;
    }
    
    public static CompoundTag getSpeciesData(Player spe) {
        CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
        if (nc == null)
            return new CompoundTag();
        return nc;
    }
    
    public static CompoundTag getManaData(Player spe) {
        CompoundTag nc = spe.getPersistentData().getCompound(MANA_DATA_ID);
        if (nc == null)
            return new CompoundTag();
        return nc;
    }
    public static class ClassData {
        public static ClassType getPlayerClassType(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                return ModClassTypes.NONE.get();
            return ModRegistries.CLASS_TYPE.get(new ResourceLocation(nc.getString("classType")));
        }
        public static boolean canRegenMana(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                return false;
            return nc.getBoolean("canRegenMana");
        }
        public static boolean isPacifist(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                return false;
            return nc.getBoolean("isPacifist");
        }
        public static ClassPassiveEffectType<?> getPassiveEffect(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                return ModClassTypes.NONE.get().classPassiveEffect().getType();
            return ModRegistries.CLASS_PASSIVE_EFFECT_TYPE.get(new ResourceLocation(nc.getString("passiveEffect")));
        }
        public static ClassAbilityType<?> getAbility(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                return ModClassTypes.NONE.get().classAbility().getType();
            return ModRegistries.CLASS_ABILITY_TYPE.get(new ResourceLocation(nc.getString("ability")));
        }
        public static void setPlayerClassType(Player spe, ClassType ct) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            if (ct == null)
                ct = ModClassTypes.NONE.get();
            if (nc.contains("class_type"))
                nc.remove("class_type");
            nc.putString("classType", ct.getClassTypeIdentifier().toString());
        }
        public static void setCanRegenMana(Player spe, boolean canRegen) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putBoolean("canRegenMana", canRegen);
        }
        public static void setPacifist(Player spe, boolean pacifist) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putBoolean("isPacifist", pacifist);
        }
        public static int getAbilityCooldown(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getInt("abilityCooldown");
        }
        
        public static void setAbilityCooldown(Player spe, int cooldown) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putInt("abilityCooldown", cooldown);
        }
        public static void decrementAbilityCooldown(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            int cooldown = nc.getInt("abilityCooldown");
            if (cooldown > 0)
                nc.putInt("abilityCooldown", cooldown - 1);
        }
        public static boolean canUseAbility(Player spe) {
            return getAbilityCooldown(spe) <= 0;
        }
        public static void setPassiveEffect(Player spe, ClassPassiveEffectType<?> passiveEffect) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            if (passiveEffect == null)
                passiveEffect = ModClassTypes.NONE.get().classPassiveEffect().getType();
            if (nc.contains("passiveEffect"))
                nc.remove("passiveEffect");
            nc.putString("passiveEffect", passiveEffect.getClassPassiveEffectTypeIdentifier().toString());
        }
        public static void setAbility(Player spe, ClassAbilityType<?> ability) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            if (ability == null)
                ability = ModClassTypes.NONE.get().classAbility().getType();
            if (nc.contains("ability"))
                nc.remove("ability");
            nc.putString("ability", ability.getClassAbilityTypeIdentifier().toString());
        }
    }
    
    public static class SpeciesData {
        public static SpeciesType getSpecies(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return ModSpeciesTypes.HUMAN.get();
            return ModRegistries.SPECIES_TYPE.get(new ResourceLocation(nc.getString("species")));
        }
        public static boolean isInvertedHealAndHarm(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return false;
            return nc.getBoolean("invertedHealAndHarm");
        }
        public static boolean shouldKeepOnDeath(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return true;
            return nc.getBoolean("keepOnDeath");
        }
        public static DietType getDiet(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return DietType.OMNIVORE;
            return DietType.valueOf(nc.getString("diet"));
        }
        public static double getMaxHealth(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("maxHealth");
        }
        public static double getAttackDamage(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("attackDamage");
        }
        public static double getAttackKnockback(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("attackKnockback");
        }
        public static double getMovementSpeed(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("movementSpeed");
        }
        public static double getArmor(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("armor");
        }
        public static double getLuck(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("luck");
        }
        public static double getBlockInteractionRange(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("blockInteractionRange");
        }
        public static double getEntityInteractionRange(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("entityInteractionRange");
        }
        public static double getGravity(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("gravity");
        }
        public static double getJumpStrength(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("jumpStrength");
        }
        public static double getKnockbackResistance(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("knockbackResistance");
        }
        public static double getSafeFallDistance(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("safeFallDistance");
        }
        public static double getScale(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 1;
            return nc.getDouble("scale");
        }
        public static double getStepHeight(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0.6;
            return nc.getDouble("stepHeight");
        }
        public static double getArmourToughness(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("armourToughness");
        }
        public static SpeciesBehaviourType<?> getBehaviour(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                return ModSpeciesTypes.HUMAN.get().speciesBehaviour().getType();
            return ModRegistries.SPECIES_BEHAVIOUR_TYPE.get(new ResourceLocation(nc.getString("behaviour")));
        }
        public static void setSpecies(Player spe, SpeciesType st) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            if (st == null)
                st = ModSpeciesTypes.HUMAN.get();
            if (nc.contains("species"))
                nc.remove("species");
            nc.putString("species", st.getSpeciesTypeIdentifier().toString());
        }
        public static void setInvertedHealAndHarm(Player spe, boolean invertedHealAndHarm) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putBoolean("invertedHealAndHarm", invertedHealAndHarm);
        }
        public static void setKeepOnDeath(Player spe, boolean keepOnDeath) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putBoolean("keepOnDeath", keepOnDeath);
        }
        public static void setDiet(Player spe, DietType diet) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            if (diet == null)
                diet = DietType.OMNIVORE;
            if (nc.contains("diet"))
                nc.remove("diet");
            nc.putString("diet", diet.toString());
        }
        public static void setMaxHealth(Player spe, double maxHealth) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("maxHealth", maxHealth);
        }
        public static void setAttackDamage(Player spe, double attackDamage) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("attackDamage", attackDamage);
        }
        public static void setAttackKnockback(Player spe, double attackKnockback) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("attackKnockback", attackKnockback);
        }
        public static void setMovementSpeed(Player spe, double movementSpeed) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("movementSpeed", movementSpeed);
        }
        public static void setArmor(Player spe, double armor) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("armor", armor);
        }
        public static void setLuck(Player spe, double luck) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("luck", luck);
        }
        public static void setBlockInteractionRange(Player spe, double blockInteractionRange) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("blockInteractionRange", blockInteractionRange);
        }
        public static void setEntityInteractionRange(Player spe, double entityInteractionRange) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("entityInteractionRange", entityInteractionRange);
        }
        public static void setGravity(Player spe, double gravity) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("gravity", gravity);
        }
        public static void setJumpStrength(Player spe, double jumpStrength) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("jumpStrength", jumpStrength);
        }
        public static void setKnockbackResistance(Player spe, double knockbackResistance) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("knockbackResistance", knockbackResistance);
        }
        public static void setSafeFallDistance(Player spe, double safeFallDistance) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("safeFallDistance", safeFallDistance);
        }
        public static void setScale(Player spe, double scale) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("scale", scale);
        }
        public static void setStepHeight(Player spe, double stepHeight) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("stepHeight", stepHeight);
        }
        public static void setArmourToughness(Player spe, double armourToughness) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("armourToughness", armourToughness);
        }
        public static void setBehaviour(Player spe, SpeciesBehaviourType<?> behaviour) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            if (behaviour == null)
                behaviour = ModSpeciesTypes.HUMAN.get().speciesBehaviour().getType();
            if (nc.contains("behaviour"))
                nc.remove("behaviour");
            nc.putString("behaviour", behaviour.getBehaviourTypeIdentifier().toString());
        }
    }
    public static class ManaData {
        public static double getMana(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(MANA_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getDouble("mana");
        }
        public static void setMana(Player spe, double mana) {
            CompoundTag nc = spe.getPersistentData().getCompound(MANA_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("mana", mana);
        }
        public static double getMaxMana(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(MANA_DATA_ID);
            if (nc == null)
                return 0;
            return nc.getInt("maxMana");
        }
        public static void setMaxMana(Player spe, double maxMana) {
            CompoundTag nc = spe.getPersistentData().getCompound(MANA_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            nc.putDouble("maxMana", maxMana);
        }
        public static double removeMana(Player spe, double manaToRemove) {
            if (manaToRemove < 0)
                manaToRemove = 0;
            setMana(spe, getMana(spe) - manaToRemove);
            return getMana(spe);
        }
        public static void healMana(Player spe, double amount) {
            amount = CommonUtils.onManaHeal(spe, amount);
            if (amount <= 0) return;
            double f = getMana(spe);
            if (f < getMaxMana(spe)) {
                setMana(spe, Math.min(f + amount, getMaxMana(spe)));
            }
        }
    }
    public static void setClassData(Player spe, CompoundTag nc) {
        spe.getPersistentData().put(CLASS_DATA_ID, nc);
    }
    public static void setSpeciesData(Player spe, CompoundTag nc) {
        spe.getPersistentData().put(SPECIES_DATA_ID, nc);
    }
    public static void setManaData(Player spe, CompoundTag nc) {
        spe.getPersistentData().put(MANA_DATA_ID, nc);
    }
    
}
