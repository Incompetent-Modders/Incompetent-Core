package com.incompetent_modders.incomp_core.api.json.spell;

import com.incompetent_modders.incomp_core.api.spell.Spell;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicReference;

public class SpellDataUtil {
    public int getManaCost(Spell spell) {
        AtomicReference<Integer> manaCost = new AtomicReference<>(0);
        SpellPropertyListener.manaCosts.forEach((s, i) -> {
            if (s.equals(spell)) {
                manaCost.set(i);
            }
        });
        return manaCost.get();
    }
    
    public int getDrawTime(Spell spell) {
        AtomicReference<Integer> drawTime = new AtomicReference<>(0);
        SpellPropertyListener.drawTimes.forEach((s, i) -> {
            if (s.equals(spell)) {
                drawTime.set(i);
            }
        });
        return drawTime.get();
    }
    
    public int getCooldown(Spell spell) {
        AtomicReference<Integer> cooldown = new AtomicReference<>(0);
        SpellPropertyListener.cooldowns.forEach((s, i) -> {
            if (s.equals(spell)) {
                cooldown.set(i);
            }
        });
        return cooldown.get();
    }
    
    public ItemStack getCatalyst(Spell spell) {
        AtomicReference<ItemStack> catalyst = new AtomicReference<>(ItemStack.EMPTY);
        SpellPropertyListener.catalysts.forEach((s, i) -> {
            if (s.equals(spell)) {
                catalyst.set(i);
            }
        });
        return catalyst.get();
    }
}
