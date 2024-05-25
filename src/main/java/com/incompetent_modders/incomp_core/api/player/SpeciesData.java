package com.incompetent_modders.incomp_core.api.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviourType;
import com.incompetent_modders.incomp_core.registry.ModAbilities;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SpeciesData {
    public static final String PLAYER_DATA_ID = IncompCore.MODID + ":PlayerData";
    
    public static class Get {
        public static class Attributes {
            public static double maxHealth(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesMaxHealth");
            }
            public static double attackDamage(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesAttackDamage");
            }
            public static double attackKnockback(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesAttackKnockback");
            }
            public static double movementSpeed(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesMovementSpeed");
            }
            public static double armor(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesArmor");
            }
            public static double luck(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesLuck");
            }
            public static double blockInteractionRange(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesBlockInteractionRange");
            }
            public static double entityInteractionRange(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesEntityInteractionRange");
            }
            public static double gravity(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesGravity");
            }
            public static double jumpStrength(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesJumpStrength");
            }
            public static double knockbackResistance(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesKnockbackResistance");
            }
            public static double safeFallDistance(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesSafeFallDistance");
            }
            public static double scale(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesScale");
            }
            public static double stepHeight(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesStepHeight");
            }
            public static double armourToughness(Player spe) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                return nc.getDouble("speciesArmourToughness");
            }
        }
        public static ResourceLocation playerSpecies(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return new ResourceLocation(nc.getString("species"));
        }
        public static boolean isInvertedHealAndHarm(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return nc.getBoolean("speciesInvertedHealAndHarm");
        }
        public static boolean shouldKeepOnDeath(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return nc.getBoolean("keepSpeciesOnDeath");
        }
        public static ResourceLocation diet(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return new ResourceLocation(nc.getString("speciesDiet"));
        }
        public static SpeciesBehaviourType<?> behaviour(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return ModRegistries.SPECIES_BEHAVIOUR_TYPE.get(new ResourceLocation(nc.getString("speciesBehaviour")));
        }
        public static AbilityType<?> ability(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return ModRegistries.ABILITY_TYPE.get(new ResourceLocation(nc.getString("speciesAbility")));
        }
        public static int abilityCooldown(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return nc.getInt("speciesAbilityCooldown");
        }
    }
    
    public static class Set {
        public static class Attributes {
            public static void maxHealth(Player spe, double maxHealth) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesMaxHealth", maxHealth);
            }
            public static void attackDamage(Player spe, double attackDamage) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesAttackDamage", attackDamage);
            }
            public static void attackKnockback(Player spe, double attackKnockback) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesAttackKnockback", attackKnockback);
            }
            public static void movementSpeed(Player spe, double movementSpeed) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesMovementSpeed", movementSpeed);
            }
            public static void armor(Player spe, double armor) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesArmor", armor);
            }
            public static void luck(Player spe, double luck) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesLuck", luck);
            }
            public static void blockInteractionRange(Player spe, double blockInteractionRange) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesBlockInteractionRange", blockInteractionRange);
            }
            public static void entityInteractionRange(Player spe, double entityInteractionRange) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesEntityInteractionRange", entityInteractionRange);
            }
            public static void gravity(Player spe, double gravity) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesGravity", gravity);
            }
            public static void jumpStrength(Player spe, double jumpStrength) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesJumpStrength", jumpStrength);
            }
            public static void knockbackResistance(Player spe, double knockbackResistance) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesKnockbackResistance", knockbackResistance);
            }
            public static void safeFallDistance(Player spe, double safeFallDistance) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesSafeFallDistance", safeFallDistance);
            }
            public static void scale(Player spe, double scale) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesScale", scale);
            }
            public static void stepHeight(Player spe, double stepHeight) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesStepHeight", stepHeight);
            }
            public static void armourToughness(Player spe, double armourToughness) {
                CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
                nc.putDouble("speciesArmourToughness", armourToughness);
            }
        }
        public static void playerSpecies(Player spe, ResourceLocation st) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            if (st == null)
                st = CommonUtils.defaultSpecies;
            if (nc.contains("species"))
                nc.remove("species");
            nc.putString("species", st.toString());
        }
        public static void isInvertedHealAndHarm(Player spe, boolean invertedHealAndHarm) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            nc.putBoolean("speciesInvertedHealAndHarm", invertedHealAndHarm);
        }
        public static void keepOnDeath(Player spe, boolean keepOnDeath) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            nc.putBoolean("keepSpeciesOnDeath", keepOnDeath);
        }
        public static void diet(Player spe, ResourceLocation diet) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            if (diet == null)
                diet = CommonUtils.defaultDiet;
            if (nc.contains("speciesDiet"))
                nc.remove("speciesDiet");
            nc.putString("speciesDiet", diet.toString());
        }
        public static void behaviour(Player spe, SpeciesBehaviourType<?> behaviour) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            if (behaviour == null)
                behaviour = SpeciesListener.getSpeciesTypeProperties(CommonUtils.defaultSpecies).behaviour().getType();
            if (nc.contains("speciesBehaviour"))
                nc.remove("speciesBehaviour");
            nc.putString("speciesBehaviour", behaviour.getBehaviourTypeIdentifier().toString());
        }
        public static void ability(Player spe, AbilityType<?> ability) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            if (ability == null)
                ability = ModAbilities.DEFAULT.get();
            if (nc.contains("speciesAbility"))
                nc.remove("speciesAbility");
            nc.putString("speciesAbility", ability.getAbilityTypeIdentifier().toString());
        }
        public static void abilityCooldown(Player spe, int cooldown) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            nc.putInt("speciesAbilityCooldown", cooldown);
        }
    }
    
    public static class Util {
        public static void decrementAbilityCooldown(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            int cooldown = nc.getInt("speciesAbilityCooldown");
            if (cooldown > 0)
                nc.putInt("speciesAbilityCooldown", cooldown - 1);
        }
        public static boolean canUseAbility(Player spe) {
            return Get.abilityCooldown(spe) <= 0;
        }
        public static boolean isSpeciesPresent(Player spe) {
            return spe.getPersistentData().getCompound(PLAYER_DATA_ID).contains("species");
        }
    }
}
