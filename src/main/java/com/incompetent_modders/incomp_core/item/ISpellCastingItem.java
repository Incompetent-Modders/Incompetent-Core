package com.incompetent_modders.incomp_core.item;

import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.api.spell.Spells;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public interface ISpellCastingItem {
    /**
     * Returns the spell currently equipped on the given itemstack. The given itemstack will be of this item.
     * @param stack The itemstack to query.
     * @return The currently equipped spell, or {@link Spells#EMPTY Spells.EMPTY} if no spell
     * is equipped.
     */
    @Nonnull
    Spell getCurrentSpell(ItemStack stack);
    
    /**
     * Returns the spell equipped in the next slot on the given itemstack. The given itemstack will be of this item.
     * @param stack The itemstack to query.
     * @return The next spell, or {@link Spells#EMPTY Spells.none} if no spell is equipped
     * in the next slot. Returns the current spell by default (useful for items with only one spell).
     */
    @Nonnull
    default Spell getNextSpell(ItemStack stack){
        return getCurrentSpell(stack);
    }
    
    /**
     * Returns the spell equipped in the previous slot on the given itemstack. The given itemstack will be of this item.
     * @param stack The itemstack to query.
     * @return The previous spell, or {@link Spells#EMPTY Spells.none} if no spell is
     * equipped in the previous slot. Returns the current spell by default (useful for items with only one spell).
     */
    @Nonnull
    default Spell getPreviousSpell(ItemStack stack){
        return getCurrentSpell(stack);
    }
    
    /**
     * Returns all the spells currently bound to the given itemstack. The given itemstack will be of this item.
     * @param stack The itemstack to query.
     * @return The bound spells, or {@link Spells#EMPTY Spells.none} if no spell
     * is equipped.
     */
    default Spell[] getSpells(ItemStack stack){
        return new Spell[]{getCurrentSpell(stack)}; // Default implementation for single-spell items, because I'm lazy
    }
    
    /**
     * Selects the next spell bound to the given itemstack. The given itemstack will be of this item.
     * @param stack The itemstack to query.
     */
    default void selectNextSpell(ItemStack stack){
        // If it doesn't need spell-switching then don't bother the implementor with it
    }
    
    /**
     * Selects the previous spell bound to the given itemstack. The given itemstack will be of this item.
     * @param stack The itemstack to query.
     */
    default void selectPreviousSpell(ItemStack stack){
        // Nothing here either
    }
    
    /**
     * Selects the spell at the given index bound to the given itemstack. The given itemstack will be of this item.
     * @param stack The itemstack to query.
     * @param index The index to set.
     * @return True if the operation succeeded, false if not.
     */
    default boolean selectSpell(ItemStack stack, int index){
        return false;
    }
    
    /**
     * Returns whether the spell HUD should be shown when a player is holding this item. Only called client-side.
     * @param player The player holding the item.
     * @param stack The itemstack to query.
     * @return True if the spell HUD should be shown, false if not.
     */
    boolean showSpellHUD(Player player, ItemStack stack);
    
    /**
     * Returns the current cooldown to display on the spell HUD for the given itemstack.
     * @param stack The itemstack to query.
     * @return The current cooldown for the equipped spell.
     */
    default int getCurrentCooldown(ItemStack stack){
        return 0;
    }
    
    /**
     * Returns the max cooldown of the current spell to display on the spell HUD for the given itemstack.
     * @param stack The itemstack to query.
     * @return The max cooldown for the equipped spell.
     */
    default int getCurrentMaxCooldown(ItemStack stack){
        return 0;
    }
    
    /**
     * Returns whether the given spell can be cast by the given stack in its current state. Does not perform any actual
     * spellcasting.
     *
     * @param stack The stack being queried; will be of this item.
     * @param spell The spell to be cast.
     * @param caster The player doing the casting.
     * @param hand The hand in which the casting item is being held.
     * @param castingTick For continuous spells, the number of ticks the spell has already been cast for. For all other
     *                    spells, this will be zero.
     * @return True if the spell can be cast, false if not.
     */
    boolean canCast(ItemStack stack, Spell spell, Player caster, InteractionHand hand, int castingTick);
    
    /**
     * Casts the given spell using the given item stack. <b>This method does not perform any checks</b>; these are done
     * in {@link ISpellCastingItem#canCast(ItemStack, Spell, Player, InteractionHand, int)}. This method
     * also performs any post-casting logic, such as mana costs and cooldowns. This method does not handle charge-up
     * times.
     * <p></p>
     * @param stack The stack being queried; will be of this item.
     * @param spell The spell to be cast.
     * @param caster The player doing the casting.
     * @param hand The hand in which the casting item is being held.
     * @param castingTick For continuous spells, the number of ticks the spell has already been cast for. For all other
     *                    spells, this will be zero.
     * @return True if the spell succeeded, false if not. This is only really for the purpose of returning a result from
     * {@link net.minecraft.world.item.Item#use(Level, Player, InteractionHand)} (World, Player, InteractionHand)} and similar methods; mana costs,
     * cooldowns and whatever else you might want to do post-spellcasting should be done within this method so that
     * external sources don't allow spells to be cast for free, for example.
     */
    boolean cast(ItemStack stack, Spell spell, Player caster, InteractionHand hand, int castingTick);
    
}
