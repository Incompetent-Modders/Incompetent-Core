package com.incompetent_modders.incomp_core.api.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PlayerDataCore {
    public static final String DATA_ID = IncompCore.MODID + ":data";
    
    public static ClassType getPlayerClassType(Player spe) {
        CompoundTag nc = spe.getPersistentData().getCompound(DATA_ID);
        if (nc == null)
            return ModClassTypes.SIMPLE_HUMAN.get();
        return ModRegistries.CLASS_TYPE.get(new ResourceLocation(nc.getString("classType")));
    }
    public static boolean canRegenMana(Player spe) {
        CompoundTag nc = spe.getPersistentData().getCompound(DATA_ID);
        if (nc == null)
            return false;
        return nc.getBoolean("canRegenMana");
    }
    public static double getMana(Player spe) {
        CompoundTag nc = spe.getPersistentData().getCompound(DATA_ID);
        if (nc == null)
            return 0;
        return nc.getDouble("mana");
    }
    public static void setPlayerClassType(Player spe, ClassType ct) {
        CompoundTag nc = spe.getPersistentData().getCompound(DATA_ID);
        if (nc == null)
            nc = new CompoundTag();
        if (ct == null)
            ct = ModClassTypes.SIMPLE_HUMAN.get();
        if (nc.contains("class_type"))
            nc.remove("class_type");
        nc.putString("classType", ct.getClassTypeIdentifier().toString());
    }
    public static void setCanRegenMana(Player spe, boolean canRegen) {
        CompoundTag nc = spe.getPersistentData().getCompound(DATA_ID);
        if (nc == null)
            nc = new CompoundTag();
        nc.putBoolean("canRegenMana", canRegen);
    }
    public static void setMana(Player spe, double mana) {
        CompoundTag nc = spe.getPersistentData().getCompound(DATA_ID);
        if (nc == null)
            nc = new CompoundTag();
        nc.putDouble("mana", mana);
    }
    
    public static CompoundTag getIncompCoreData(Player spe) {
        CompoundTag nc = spe.getPersistentData().getCompound(DATA_ID);
        if (nc == null)
            return new CompoundTag();
        return nc;
    }
    public static void setIncompCoreData(Player spe, CompoundTag nc) {
        spe.getPersistentData().put(DATA_ID, nc);
    }
}
