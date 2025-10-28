package com.incompetent_modders.incomp_core.core.events.forge;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public class ItemEvents {

    @SubscribeEvent
    public void onItemStartUse(LivingEntityUseItemEvent.Start event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();
    }
}
