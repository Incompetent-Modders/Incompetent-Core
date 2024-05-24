package com.incompetent_modders.incomp_core.api.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffectType;
import com.incompetent_modders.incomp_core.registry.ModAbilities;
import com.incompetent_modders.incomp_core.registry.ModClassPassiveEffects;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ClassData {
    public static final String PLAYER_DATA_ID = IncompCore.MODID + ":PlayerData";
    
    public static class Set {
        public static void playerClassType(Player spe, ResourceLocation ct) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            if (ct == null)
                ct = CommonUtils.defaultClass;
            if (nc.contains("class_type"))
                nc.remove("class_type");
            nc.putString("classType", ct.toString());
        }
        public static void canRegenMana(Player spe, boolean canRegen) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            nc.putBoolean("canClassRegenMana", canRegen);
        }
        public static void isPacifist(Player spe, boolean pacifist) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            nc.putBoolean("isClassPacifist", pacifist);
        }
        public static void abilityCooldown(Player spe, int cooldown) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            nc.putInt("classAbilityCooldown", cooldown);
        }
        public static void passiveEffect(Player spe, ClassPassiveEffectType<?> passiveEffect) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            if (passiveEffect == null)
                passiveEffect = ModClassPassiveEffects.DEFAULT.get();
            if (nc.contains("classPassiveEffect"))
                nc.remove("classPassiveEffect");
            nc.putString("classPassiveEffect", passiveEffect.getClassPassiveEffectTypeIdentifier().toString());
        }
        public static void ability(Player spe, AbilityType<?> ability) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            if (ability == null)
                ability = ModAbilities.DEFAULT.get();
            if (nc.contains("classAbility"))
                nc.remove("classAbility");
            nc.putString("classAbility", ability.getAbilityTypeIdentifier().toString());
        }
    }
    
    public static class Get {
        public static ResourceLocation playerClassType(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return new ResourceLocation(nc.getString("classType"));
        }
        public static boolean canRegenMana(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return nc.getBoolean("canClassRegenMana");
        }
        public static boolean isPacifist(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return nc.getBoolean("isClassPacifist");
        }
        public static int abilityCooldown(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return nc.getInt("classAbilityCooldown");
        }
        public static ClassPassiveEffectType<?>  passiveEffect(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return ModRegistries.CLASS_PASSIVE_EFFECT_TYPE.get(new ResourceLocation(nc.getString("classPassiveEffect")));
        }
        public static AbilityType<?> ability(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return ModRegistries.ABILITY_TYPE.get(new ResourceLocation(nc.getString("classAbility")));
        }
    }
    
    public static class Util {
        public static boolean canUseAbility(Player spe) {
            return Get.abilityCooldown(spe) <= 0;
        }
        public static void decrementAbilityCooldown(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            int cooldown = nc.getInt("classAbilityCooldown");
            if (cooldown > 0)
                nc.putInt("classAbilityCooldown", cooldown - 1);
        }
    }
}
