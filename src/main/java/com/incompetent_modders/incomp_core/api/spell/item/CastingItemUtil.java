package com.incompetent_modders.incomp_core.api.spell.item;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.item.ItemSpellSlots;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellProperties;
import com.incompetent_modders.incomp_core.client.managers.ClientSpellManager;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class CastingItemUtil {
    public static ResourceLocation emptySpell = IncompCore.makeId("empty");
    public static Map<Integer, ResourceLocation> singleEmptySpell = Map.of(0, emptySpell);
    
    public static ResourceLocation deserializeFromSlot(ItemStack stack, int slot) {
        ItemSpellSlots spellSlots = stack.getOrDefault(ModDataComponents.SPELLS, ItemSpellSlots.EMPTY);
        return spellSlots.getSpell(slot);
    }
    public static void serializeToSlot(ItemStack stack, int slot, ResourceLocation spell) {
        ItemSpellSlots spellSlots = stack.getOrDefault(ModDataComponents.SPELLS, ItemSpellSlots.EMPTY);
        ItemSpellSlots.Entry entry = new ItemSpellSlots.Entry(spell, slot);
        stack.set(ModDataComponents.SPELLS, spellSlots.withSpellAdded(entry));
    }
    
    public static int getSelectedSpellSlot(ItemStack stack) {
        if (!stack.has(ModDataComponents.SELECTED_SPELL_SLOT)) {
            return 0;
        }
        return stack.getOrDefault(ModDataComponents.SELECTED_SPELL_SLOT, 0);
    }
    
    public static ResourceLocation getSelectedSpell(ItemStack stack) {
        return deserializeFromSlot(stack, getSelectedSpellSlot(stack));
    }
    public static SpellProperties getClientSpellProperties(ItemStack stack) {
        return ClientSpellManager.getInstance().getSpellProperties(getSelectedSpell(stack));
    }
    public static SpellProperties getServerSpellProperties(ItemStack stack) {
        return SpellListener.getSpellProperties(getSelectedSpell(stack));
    }
}
