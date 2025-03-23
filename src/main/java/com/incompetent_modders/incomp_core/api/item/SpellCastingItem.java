package com.incompetent_modders.incomp_core.api.item;

import com.incompetent_modders.incomp_core.client.util.ClientUtil;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.incompetent_modders.incomp_core.core.def.Spell;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class SpellCastingItem extends Item {
    
    private final int maxSpellSlots;
    
    public SpellCastingItem(Properties properties, int maxSpellSlots) {
        super(properties);
        this.maxSpellSlots = maxSpellSlots;
    }
    
    public int getMaxSpellSlots() {
        return maxSpellSlots;
    }
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
    
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return CastingItemUtil.getSelectedSpellInstance(stack, entity).definition().drawTime();
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }
    public int spellRemainingDrawTime(ItemStack stack) {
        return getCastProgress(stack);
    }
    private static boolean casting = false;
    
    public void onUseTick(Level level, LivingEntity entity, ItemStack itemstack, int p_41431_) {
        if (getCastProgress(itemstack) > 0) {
            decrementCastProgress(itemstack);
        }
        if (getCastProgress(itemstack) == 0 && !casting) {
            Spell spell = CastingItemUtil.getSelectedSpellInstanceWithKey(itemstack, entity).getSecond();
            if (spell != null && !casting) {
                casting = true;
                if (entity instanceof Player player) {
                    IncompCore.LOGGER.info("Executing spell: {}", Spell.getDisplayName(CastingItemUtil.getSelectedSpellInstanceWithKey(itemstack, entity).getFirst()).getString());
                    spell.executeCast(player);
                    casting = false;
                }
            }
            IncompCore.LOGGER.info("Casting has finished.");
            entity.stopUsingItem();
        }
    }
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack castingStack = player.getItemInHand(hand);
        boolean noCastProgress = SpellCastingItem.getCastProgress(castingStack) == 0;
        boolean isOffhand = player.getItemInHand(hand) == castingStack && hand == InteractionHand.OFF_HAND;
        boolean isBlankSpell = CastingItemUtil.getSelectedSpellInstance(castingStack, player).isBlankSpell();
         if (isOffhand) {
            IncompCore.LOGGER.info("Offhand casting is not allowed.");
            return InteractionResultHolder.fail(castingStack);
        } if (isBlankSpell) {
            IncompCore.LOGGER.info("No spell selected.");
            return InteractionResultHolder.fail(castingStack);
        }
        if (noCastProgress) {
            IncompCore.LOGGER.info("Starting cast progress.");
            SpellCastingItem.setCastProgress(castingStack, player);
            player.getCooldowns().addCooldown(this, getUseDuration(castingStack, player));
        } else  {
            Spell spell = CastingItemUtil.getSelectedSpellInstanceWithKey(castingStack, player).getSecond();
            player.startUsingItem(hand);
            IncompCore.LOGGER.info("Player has selected spell: {}", Spell.getDisplayName(CastingItemUtil.getSelectedSpellInstanceWithKey(castingStack, player).getFirst()).getString());
            spell.executeCast(player);
        }
        return InteractionResultHolder.consume(castingStack);
    }
    
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        HolderLookup.Provider provider = context.registries();
        if (provider != null) {
            ClientUtil.createSelectedSpellTooltip(tooltip, stack, provider);
        }
        if (stack.getOrDefault(ModDataComponents.MAX_SPELL_SLOTS, getMaxSpellSlots()) > 1) {
            ClientUtil.createAvailableSpellsTooltip(tooltip, stack);
        }
    }
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        if (!stack.has(ModDataComponents.SELECTED_SPELL_SLOT)) {
            stack.set(ModDataComponents.SELECTED_SPELL_SLOT, 0);
        }
        if (!stack.has(ModDataComponents.REMAINING_DRAW_TIME) && entity instanceof LivingEntity livingEntity) {
            stack.set(ModDataComponents.REMAINING_DRAW_TIME, CastingItemUtil.getSelectedSpellInstance(stack, livingEntity).definition().drawTime());
        }
        if (!stack.has(ModDataComponents.MAX_SPELL_SLOTS)) {
            stack.set(ModDataComponents.MAX_SPELL_SLOTS, getMaxSpellSlots());
        }
        if (!stack.has(ModDataComponents.SPELLS)) {
            stack.set(ModDataComponents.SPELLS, ItemSpellSlots.EMPTY);
        }
    }
    public static ResourceKey<Spell> getSelectedSpell(ItemStack stack) {
        return CastingItemUtil.deserializeFromSlot(stack, CastingItemUtil.getSelectedSpellSlot(stack));
    }
    
    public static String getSpellNameInSlot(ItemStack stack, int slot) {
        return Spell.getDisplayName(CastingItemUtil.deserializeFromSlot(stack, slot)).getString();
    }
    
    public void releaseUsing(ItemStack itemstack, Level level, LivingEntity entity, int timeLeft) {
        resetCastProgress(itemstack);
    }
    
    public static int getCastProgress(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.REMAINING_DRAW_TIME, 0);
    }
    
    public static void setCastProgress(ItemStack stack, LivingEntity entity) {
        int ticks = CastingItemUtil.getSelectedSpellInstance(stack, entity).definition().drawTime();
        if (ticks == 0) {
            IncompCore.LOGGER.warn("Spell draw time is 0 for {}", stack);
        }
        stack.set(ModDataComponents.REMAINING_DRAW_TIME, ticks);
        IncompCore.LOGGER.info("Set cast progress to {} for {}", ticks, stack);
    }
    
    private void resetCastProgress(ItemStack stack) {
        stack.set(ModDataComponents.REMAINING_DRAW_TIME, 0);
        IncompCore.LOGGER.info("Reset cast progress for {}", stack);
    }
    private void decrementCastProgress(ItemStack stack) {
        DataComponentMap components = stack.getComponents();
        int ticks = components.getOrDefault(ModDataComponents.REMAINING_DRAW_TIME.get(), 0);
        if (ticks > 0) {
            IncompCore.LOGGER.info("Decrementing cast progress for {}", stack);
            stack.set(ModDataComponents.REMAINING_DRAW_TIME, ticks - 1);
        }
    }
}
