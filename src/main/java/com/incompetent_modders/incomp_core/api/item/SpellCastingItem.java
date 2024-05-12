package com.incompetent_modders.incomp_core.api.item;

import com.incompetent_modders.incomp_core.ClientUtil;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.spell.*;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.registry.ModSpells;
import com.incompetent_modders.incomp_core.util.ModDataComponents;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;

public class SpellCastingItem extends Item {
    public SpellCastingItem(Properties properties) {
        super(properties);
    }
    
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
    
    public int getUseDuration(ItemStack stack) {
        return CastingItemUtil.getSelectedSpell(stack).getDrawTime();
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
            Spell spell = getSelectedSpell(itemstack);
            if (spell != null && !casting) {
                casting = true;
                if (entity instanceof Player player) {
                    spell.getSpellProperties().executeCast(player);
                    casting = false;
                }
            }
            entity.stopUsingItem();
        }
    }
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack castingStack = player.getItemInHand(hand);
        if (SpellCastingItem.getCastProgress(castingStack) == 0) {
            SpellCastingItem.setCastProgress(CastingItemUtil.getSelectedSpell(castingStack), castingStack);
        }
        if (player.getItemInHand(hand) == castingStack && hand == InteractionHand.OFF_HAND) {
            IncompCore.LOGGER.info("Offhand casting is not allowed.");
            return InteractionResultHolder.fail(castingStack);
        }
        if (CastingItemUtil.getSelectedSpell(castingStack).getSpellProperties().isBlankSpell()) {
            IncompCore.LOGGER.info("No spell selected.");
            return InteractionResultHolder.fail(castingStack);
        }
        player.startUsingItem(hand);
        IncompCore.LOGGER.info("Player has begun casting spell: {}", CastingItemUtil.getSelectedSpell(castingStack).getDisplayName().getString());
        return InteractionResultHolder.consume(castingStack);
    }
    
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ClientUtil.createSelectedSpellTooltip(tooltip, stack);
        ClientUtil.createAvailableSpellsTooltip(tooltip, stack, this);
    }
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        if (CastingItemUtil.getCustomData(stack) == null) {
            return;
        }
        for (int i = 0; i < stack.getOrDefault(ModDataComponents.MAX_SPELL_SLOTS, 6); i++) {
            String spellSlot_NBT = "spellSlot_";
            if (!CastingItemUtil.getCustomData(stack).contains(spellSlot_NBT + i) || CastingItemUtil.getCustomData(stack).copyTag().getString(spellSlot_NBT + i).isEmpty()) {
                int finalI = i;
                CustomData.update(DataComponents.CUSTOM_DATA, stack, (tag) -> {
                    tag.putString(spellSlot_NBT + finalI, ModSpells.EMPTY.get().getSpellIdentifier().toString());
                });
            }
            if (CastingItemUtil.deserializeFromSlot(stack, i) == null) {
                int finalI = i;
                CustomData.update(DataComponents.CUSTOM_DATA, stack, (tag) -> {
                    tag.putString(spellSlot_NBT + finalI, ModSpells.EMPTY.get().getSpellIdentifier().toString());
                });
            }
        }
        String selSpellSlot_NBT = "selectedSpellSlot";
        if (!CastingItemUtil.getCustomData(stack).contains(selSpellSlot_NBT)) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, (tag) -> {
                tag.putInt(selSpellSlot_NBT, 0);
            });
        }
        
        if (!stack.has(ModDataComponents.REMAINING_DRAW_TIME)) {
            stack.set(ModDataComponents.REMAINING_DRAW_TIME, getUseDuration(stack));
        }
        if (!stack.has(ModDataComponents.MAX_SPELL_SLOTS)) {
            stack.set(ModDataComponents.MAX_SPELL_SLOTS, 6);
        }
    }
    public static Spell getSelectedSpell(ItemStack stack) {
        return CastingItemUtil.deserializeFromSlot(stack, CastingItemUtil.getSelectedSpellSlot(stack));
    }
    
    public static String getSpellNameInSlot(ItemStack stack, int slot) {
        return CastingItemUtil.deserializeFromSlot(stack, slot).getDisplayName().getString();
    }
    
    public void releaseUsing(ItemStack itemstack, Level level, LivingEntity entity, int timeLeft) {
        resetCastProgress(itemstack);
    }
    
    public static int getCastProgress(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.REMAINING_DRAW_TIME, 0);
    }
    
    public static void setCastProgress(Spell spell, ItemStack stack) {
        int ticks = spell.getDrawTime();
        stack.set(ModDataComponents.REMAINING_DRAW_TIME, ticks);
    }
    
    private void resetCastProgress(ItemStack stack) {
        stack.set(ModDataComponents.REMAINING_DRAW_TIME, 0);
    }
    private void decrementCastProgress(ItemStack stack) {
        DataComponentMap components = stack.getComponents();
        int ticks = components.getOrDefault(ModDataComponents.REMAINING_DRAW_TIME.get(), 0);
        if (ticks > 0) {
            stack.set(ModDataComponents.REMAINING_DRAW_TIME, ticks - 1);
        }
    }
}
