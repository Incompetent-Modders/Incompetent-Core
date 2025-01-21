package com.incompetent_modders.incomp_core.api.spell.item;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.item.ItemSpellSlots;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.incompetent_modders.incomp_core.core.def.Spell;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class CastingItemUtil {
    public static ResourceLocation emptySpell = IncompCore.makeId("empty");
    public static Map<Integer, ResourceLocation> singleEmptySpell = Map.of(0, emptySpell);
    
    public static ResourceKey<Spell> deserializeFromSlot(ItemStack stack, int slot) {
        ItemSpellSlots spellSlots = stack.getOrDefault(ModDataComponents.SPELLS, ItemSpellSlots.EMPTY);
        return spellSlots.getSpell(slot);
    }
    public static void serializeToSlot(ItemStack stack, int slot,  ResourceKey<Spell> spell) {
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
    
    public static ResourceKey<Spell> getSelectedSpell(ItemStack stack) {
        return deserializeFromSlot(stack, getSelectedSpellSlot(stack));
    }

    public static Spell getSelectedSpellInstance(ItemStack stack, LivingEntity entity) {
        Registry<Spell> spellRegistry = entity.registryAccess().registryOrThrow(ModRegistries.Keys.SPELL);
        return spellRegistry.getOrThrow(getSelectedSpell(stack));
    }

    public static Pair<ResourceKey<Spell>, Spell> getSelectedSpellInstanceWithKey(ItemStack stack, LivingEntity entity) {
        Registry<Spell> spellRegistry = entity.registryAccess().registryOrThrow(ModRegistries.Keys.SPELL);
        ResourceKey<Spell> key = getSelectedSpell(stack);
        return Pair.of(key, spellRegistry.getOrThrow(key));
    }
}
