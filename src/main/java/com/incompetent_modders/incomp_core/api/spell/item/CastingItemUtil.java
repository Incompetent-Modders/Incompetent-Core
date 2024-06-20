package com.incompetent_modders.incomp_core.api.spell.item;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellProperties;
import com.incompetent_modders.incomp_core.client.managers.ClientSpellManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.fml.loading.FMLEnvironment;

public class CastingItemUtil {
    public static ResourceLocation emptySpell = IncompCore.makeId("empty");
    public static CustomData getCustomData(ItemStack stack) {
        return stack.get(DataComponents.CUSTOM_DATA);
    }
    @SuppressWarnings("deprecation")
    public static CompoundTag getCustomDataTag(ItemStack stack) {
        getCustomData(stack);
        return getCustomData(stack).getUnsafe();
    }
    public static ResourceLocation deserializeFromSlot(ItemStack stack, int slot) {
        if (getCustomData(stack) == null) {
            IncompCore.LOGGER.warn("[DEBUG CastingItemUtil.deserializeFromSlot(ItemStack stack, int slot)] Custom data is null for {}", stack);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            IncompCore.LOGGER.warn("[DEBUG CastingItemUtil.deserializeFromSlot(ItemStack stack, int slot)] Custom data is now {}", stack.get(DataComponents.CUSTOM_DATA));
            return emptySpell;
        }
        if (getCustomData(stack).contains("spellSlot_" + slot)) {
            //The NBT formats the spells as modid:spellname. We need to separate them into two strings.
            String spellModid = getCustomData(stack).copyTag().getString("spellSlot_" + slot).split(":")[0];
            String spellName = getCustomData(stack).copyTag().getString("spellSlot_" + slot).split(":")[1];
            return ResourceLocation.fromNamespaceAndPath(spellModid, spellName);
        }
        IncompCore.LOGGER.warn("[DEBUG CastingItemUtil.deserializeFromSlot(ItemStack stack, int slot)] No spell in slot {} for {}", slot, stack);
        return emptySpell;
    }
    public static void serializeToSlot(ItemStack stack, int slot, ResourceLocation spell) {
        if (getCustomData(stack) == null) {
            return;
        }
        CustomData.update(DataComponents.CUSTOM_DATA, stack, (tag) -> {
            tag.putString("spellSlot_" + slot, spell.toString());
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
