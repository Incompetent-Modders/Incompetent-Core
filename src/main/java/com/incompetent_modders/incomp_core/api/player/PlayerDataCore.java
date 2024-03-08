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
        return ModRegistries.CLASS_TYPE.get(new ResourceLocation(nc.getString("class_type")));
    }
    
    public static void setPlayerClassType(Player spe, ClassType ct) {
        CompoundTag nc = spe.getPersistentData().getCompound(DATA_ID);
        if (nc == null)
            nc = new CompoundTag();
        if (ct == null)
            ct = ModClassTypes.SIMPLE_HUMAN.get();
        nc.putString("class_type", ct.getClassTypeIdentifier().toString());
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
