package com.incompetent_modders.incomp_core.api.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PlayerDataCore {
    public static final String CLASS_DATA_ID = IncompCore.MODID + ":ClassData";
    public static final String MANA_DATA_ID = IncompCore.MODID + ":ManaData";
    public static CompoundTag getClassData(Player spe) {
        CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
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
                return ModClassTypes.SIMPLE_HUMAN.get();
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
        public static void setPlayerClassType(Player spe, ClassType ct) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (nc == null)
                nc = new CompoundTag();
            if (ct == null)
                ct = ModClassTypes.SIMPLE_HUMAN.get();
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
    public static void setManaData(Player spe, CompoundTag nc) {
        spe.getPersistentData().put(MANA_DATA_ID, nc);
    }
}
