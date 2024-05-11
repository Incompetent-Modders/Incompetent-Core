package com.incompetent_modders.incomp_core.api.spell.item;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.registry.ModSpells;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

public class CastingItemUtil {
    public static CustomData getCustomData(ItemStack stack) {
        return stack.get(DataComponents.CUSTOM_DATA);
    }
    @SuppressWarnings("deprecation")
    public static CompoundTag getCustomDataTag(ItemStack stack) {
        getCustomData(stack);
        return getCustomData(stack).getUnsafe();
    }
    public static Spell deserializeFromSlot(ItemStack stack, int slot) {
        if (getCustomData(stack) == null) {
            return ModSpells.EMPTY.get();
        }
        if (getCustomData(stack).contains("spellSlot_" + slot)) {
            //The NBT formats the spells as modid:spellname. We need to separate them into two strings.
            String spellModid = getCustomData(stack).copyTag().getString("spellSlot_" + slot).split(":")[0];
            String spellName = getCustomData(stack).copyTag().getString("spellSlot_" + slot).split(":")[1];
            return ModRegistries.SPELL.get(new ResourceLocation(spellModid, spellName));
        }
        return ModSpells.EMPTY.get();
        
    }
    public static void serializeToSlot(ItemStack stack, int slot, Spell spell) {
        if (getCustomData(stack) == null) {
            return;
        }
        CustomData.update(DataComponents.CUSTOM_DATA, stack, (tag) -> {
            tag.putString("spellSlot_" + slot, spell.getSpellIdentifier().toString());
        });
    }
    
    public static int getSelectedSpellSlot(ItemStack stack) {
        if (getCustomData(stack) == null) {
            return 0;
        }
        if (getCustomData(stack).contains("selectedSpellSlot")) {
            return getCustomData(stack).copyTag().getInt("selectedSpellSlot");
        }
        return 0;
    }
    public static Spell getSelectedSpell(ItemStack stack) {
        return deserializeFromSlot(stack, getSelectedSpellSlot(stack));
    }
}
