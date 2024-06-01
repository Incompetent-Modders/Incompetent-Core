package com.incompetent_modders.incomp_core.api.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.common.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class ManaData {
    public static final String PLAYER_DATA_ID = IncompCore.MODID + ":PlayerData";
    
    public static class Set {
        public static void mana(Player spe, double mana) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            nc.putDouble("mana", mana);
        }
        public static void maxMana(Player spe, double maxMana) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            nc.putDouble("maxMana", maxMana);
        }
    }
    
    public static class Get {
        public static double mana(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return nc.getDouble("mana");
        }
        public static double maxMana(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(PLAYER_DATA_ID);
            return nc.getInt("maxMana");
        }
    }
    
    public static class Util {
        public static double removeMana(Player spe, double manaToRemove) {
            if (manaToRemove < 0)
                manaToRemove = 0;
            Set.mana(spe, Get.mana(spe) - manaToRemove);
            return Get.mana(spe);
        }
        public static void healMana(Player spe, double amount) {
            amount = Utils.onManaHeal(spe, amount);
            if (amount <= 0) return;
            double f = Get.mana(spe);
            if (f < Get.maxMana(spe)) {
                Set.mana(spe, Math.min(f + amount, Get.maxMana(spe)));
            }
        }
    }
}
