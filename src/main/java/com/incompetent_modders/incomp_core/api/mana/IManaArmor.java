package com.incompetent_modders.incomp_core.api.mana;

import net.minecraft.world.item.ItemStack;

public interface IManaArmor {
    default int getMaxManaBoost(ItemStack i) {
        return 0;
    }
    
    default int getManaRegenBoost(ItemStack i) {
        return 0;
    }
    
}
