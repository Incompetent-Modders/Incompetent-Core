package com.incompetent_modders.incomp_core.core.def.attributes;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public interface PreventEquip {

    boolean canEquip(LivingEntity entity, ItemStack stack, EquipmentSlot slot);

    default Component tooltip(ItemStack itemStack, TooltipFlag flags, Item.TooltipContext context) {
        return Component.translatable("incompetent_core.tooltip.cannot_wear").setStyle(Style.EMPTY.withItalic(true).withColor(0xcd0016));
    }
}
