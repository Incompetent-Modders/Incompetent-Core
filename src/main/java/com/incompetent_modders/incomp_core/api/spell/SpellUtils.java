package com.incompetent_modders.incomp_core.api.spell;

import com.incompetent_modders.incomp_core.registry.ModCapabilities;
import com.incompetent_modders.incomp_core.ModRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SpellUtils {
    public static void giveItems(Level level, Player player, int amount, Item item) {
        ItemStack stack = new ItemStack(item);
        stack.setCount(amount);
        if (!level.isClientSide) {
            player.getInventory().add(stack);
        }
    }
    public static boolean isInventoryFull(Player player) {
        return player.getInventory().getFreeSlot() == -1;
    }
    
    public static void removeMana(Player player, int amount) {
        ModCapabilities.getMana(player).ifPresent(cap -> cap.removeMana(amount));
    }
    
    public static Spell deserializeFromSlot(CompoundTag tag, int slot) {
        if (tag == null) {
            return Spells.EMPTY.get();
        }
        if (tag.contains("spellSlot_" + slot)) {
            return ModRegistries.SPELL.get(new ResourceLocation(tag.getString("spellSlot_" + slot)));
        }
        return Spells.EMPTY.get();
        
    }
}
