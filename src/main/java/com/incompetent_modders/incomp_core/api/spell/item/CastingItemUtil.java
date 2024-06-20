package com.incompetent_modders.incomp_core.api.spell.item;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellProperties;
import com.incompetent_modders.incomp_core.client.managers.ClientSpellManager;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.HashMap;
import java.util.Map;

public class CastingItemUtil {
    public static ResourceLocation emptySpell = IncompCore.makeId("empty");
    public static Map<Integer, ResourceLocation> singleEmptySpell = Map.of(0, emptySpell);
    
    public static ResourceLocation deserializeFromSlot(ItemStack stack, int slot) {
        if (!stack.has(ModDataComponents.SPELLS)) {
            IncompCore.LOGGER.warn("[DEBUG CastingItemUtil.deserializeFromSlot(ItemStack stack, int slot)] No spells component in {}", stack);
            Map<Integer, ResourceLocation> spells = new HashMap<>();
            int maxSpellSlots = stack.getOrDefault(ModDataComponents.MAX_SPELL_SLOTS, 0);
            for (int i = 0; i <= maxSpellSlots; i++) {
                spells.put(i, CastingItemUtil.emptySpell);
            }
            stack.set(ModDataComponents.SPELLS, spells);
            IncompCore.LOGGER.warn("[DEBUG CastingItemUtil.deserializeFromSlot(ItemStack stack, int slot)] Added spells component to {}", stack);
            return emptySpell;
        }
        if (stack.has(ModDataComponents.SPELLS)) {
            return stack.getOrDefault(ModDataComponents.SPELLS, singleEmptySpell).getOrDefault(slot, emptySpell);
        }
        IncompCore.LOGGER.warn("[DEBUG CastingItemUtil.deserializeFromSlot(ItemStack stack, int slot)] No spell in slot {} for {}", slot, stack);
        return emptySpell;
    }
    public static void serializeToSlot(ItemStack stack, int slot, ResourceLocation spell) {
        if (!stack.has(ModDataComponents.SPELLS)) {
            IncompCore.LOGGER.warn("[DEBUG CastingItemUtil.serializeToSlot(ItemStack stack, int slot, ResourceLocation spell)] No spells component in {}", stack);
            Map<Integer, ResourceLocation> spells = new HashMap<>();
            int maxSpellSlots = stack.getOrDefault(ModDataComponents.MAX_SPELL_SLOTS, 0);
            for (int i = 0; i <= maxSpellSlots; i++) {
                spells.put(i, CastingItemUtil.emptySpell);
            }
            stack.set(ModDataComponents.SPELLS, spells);
            IncompCore.LOGGER.warn("[DEBUG CastingItemUtil.serializeToSlot(ItemStack stack, int slot, ResourceLocation spell)] Added spells component to {}", stack);
        }
        if (stack.has(ModDataComponents.SPELLS)) {
            stack.getOrDefault(ModDataComponents.SPELLS, singleEmptySpell).put(slot, spell);
        }
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
        return ClientSpellManager.getSpellProperties(getSelectedSpell(stack));
    }
    public static SpellProperties getServerSpellProperties(ItemStack stack) {
        return SpellListener.getSpellProperties(getSelectedSpell(stack));
    }
    public static int getSpellDrawTime(ItemStack stack) {
        SpellProperties properties = getClientSpellProperties(stack);
        SpellProperties serverProperties = getServerSpellProperties(stack);
        if (properties == null) {
            IncompCore.LOGGER.warn("[DEBUG CastingItemUtil.getSpellDrawTime(ItemStack stack)] Spell properties are null for {}", getSelectedSpell(stack));
            return 0;
        }
        if (FMLEnvironment.dist.isClient()) {
            return serverProperties.drawTime();
        }
        return properties.drawTime();
    }
    public static boolean isBlankSpell(ItemStack stack) {
        SpellProperties properties = getClientSpellProperties(stack);
        SpellProperties serverProperties = getServerSpellProperties(stack);
        if (properties == null) {
            IncompCore.LOGGER.warn("[DEBUG CastingItemUtil.isBlankSpell(ItemStack stack)] Spell properties are null for {}", getSelectedSpell(stack));
            return true;
        }
        if (FMLEnvironment.dist.isClient()) {
            return serverProperties.isBlankSpell();
        }
        return properties.isBlankSpell();
    }
}
