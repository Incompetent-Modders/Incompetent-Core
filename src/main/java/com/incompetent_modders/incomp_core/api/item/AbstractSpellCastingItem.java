package com.incompetent_modders.incomp_core.api.item;

import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.api.spell.SpellUtils;
import com.incompetent_modders.incomp_core.api.spell.Spells;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static com.incompetent_modders.incomp_core.IncompCore.*;

public abstract class AbstractSpellCastingItem extends Item {
    private final String selSpellSlot = "selectedSpellSlot";
    private final String spellSlot = "spellSlot_";
    private final String spellSlotCooldown = "spellSlotCoolDown_";
    private final String remainingDrawTime = "castProgress";
    private final int level;
    private static int selectedSpellSlot;
    public AbstractSpellCastingItem(Properties properties, int level) {
        super(properties);
        this.level = level;
    }
    
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
    
    public int getLevel() {
        return this.level;
    }
    public int getUseDuration(ItemStack stack) {
        return getSelectedSpell(stack).getDrawTime();
    }
    public int getSpellCoolDown(ItemStack stack) {
        return getSelectedSpell(stack).getCoolDown();
    }
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }
    public int spellRemainingDrawTime(ItemStack stack) {
        return getCastProgress(stack);
    }
    public void onUseTick(Level level, LivingEntity entity, ItemStack itemstack, int p_41431_) {
        if (getCastProgress(itemstack) > 0) {
            decrementCastProgress(itemstack);
        }
        if (getCastProgress(itemstack) == 0) {
            Spell spell = getSelectedSpell(itemstack);
            if (spell != null && !isCoolDown(selectedSpellSlot, itemstack)) {
                spell.cast(level, entity, InteractionHand.MAIN_HAND, false);
                level.playSound((Player) entity, entity.getX(), entity.getY(), entity.getZ(), spell.getSpellSound(), entity.getSoundSource(), 1.0F, 1.0F);
                addCoolDown(selectedSpellSlot, getSpellCoolDown(itemstack), itemstack);
            }
            entity.stopUsingItem();
        }
    }
    
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (getCastProgress(itemstack) == 0) {
            setCastProgress(getSelectedSpell(itemstack), itemstack);
        }
        if (isCoolDown(selectedSpellSlot, itemstack))
            return InteractionResultHolder.fail(itemstack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }
    public static int getSpellSlots(int tier) {
        return switch (tier) {
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            default -> 1;
        };
    }
    
    public static String getSpellNameInSlot(CompoundTag tag, int slot) {
        return SpellUtils.deserializeFromSlot(tag, slot).getDisplayName().getString();
    }
    
    private static final Component SELECTED_SPELL_TITLE = Component.translatable(
                    Util.makeDescriptionId("item", new ResourceLocation(MODID,"staff.selected_spell"))
            )
            .withStyle(TITLE_FORMAT);
    private static final Component AVAILABLE_SPELLS_TITLE = Component.translatable(
                    Util.makeDescriptionId("item", new ResourceLocation(MODID,"staff.available_spells"))
            )
            .withStyle(TITLE_FORMAT);
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        String selectedSlotCoolDown = CommonUtils.timeFromTicks(getCoolDown(selectedSpellSlot, stack), 2);
        tooltip.add(CommonComponents.EMPTY);
        tooltip.add(SELECTED_SPELL_TITLE);
        tooltip.add(CommonComponents.space().append(getSelectedSpell(stack).getDisplayName()).withStyle(DESCRIPTION_FORMAT).append(getCoolDown(selectedSpellSlot, stack) > 0 ? " - " + selectedSlotCoolDown : "").withStyle(DESCRIPTION_FORMAT));
        tooltip.add(CommonComponents.EMPTY);
        tooltip.add(AVAILABLE_SPELLS_TITLE);
        for (int i = 0; i <= getSpellSlots(this.getLevel()) + 1; i++) {
            
            if (i == selectedSpellSlot) {
                return;
            }
            else {
                String slotCoolDown = CommonUtils.timeFromTicks(getCoolDown(i, stack), 2);
                tooltip.add(CommonComponents.space().append(getSpellNameInSlot(tag, i)).withStyle(DESCRIPTION_FORMAT).append(getCoolDown(i, stack) > 0 ? " - " + slotCoolDown : "").withStyle(DESCRIPTION_FORMAT));
            }
        }
        
    }
    
    public void releaseUsing(ItemStack itemstack, Level level, LivingEntity entity, int timeLeft) {
        resetCastProgress(itemstack);
    }
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        CompoundTag tag = stack.getOrCreateTag();
        
        
        
        for (int i = 0; i <= getSpellSlots(this.getLevel()); i++) {
            if (!tag.contains(spellSlot + i) || tag.getString(spellSlot + i).isEmpty()) {
                tag.putString(spellSlot + i, Spells.EMPTY.get().getSpellIdentifier().toString());
            }
            decrementCoolDowns(stack, level, (Player) entity);
            
        }
        if (!tag.contains(selSpellSlot))
            tag.putInt(selSpellSlot, 1);
    }
    public void changeSelectedSpell(ItemStack stack, boolean up) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return;
        }
        int selectedSpellSlotTag = tag.getInt(selSpellSlot);
        if (up) {
            if (selectedSpellSlotTag == getSpellSlots(this.getLevel())) {
                selectedSpellSlot = 0;
            } else {
                selectedSpellSlot++;
            }
        }
        if (!up) {
            if (selectedSpellSlotTag == 0) {
                selectedSpellSlot = getSpellSlots(this.getLevel());
            } else {
                selectedSpellSlot--;
            }
        }
        tag.putInt(selSpellSlot, selectedSpellSlot);
    }
    public Spell getSelectedSpell(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            return SpellUtils.deserializeFromSlot(tag, selectedSpellSlot);
        }
        return Spells.EMPTY.get();
    }
    private void addCoolDown(int slot, int ticks, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        switch (slot) {
            case 0 -> tag.putInt(spellSlotCooldown + 0, ticks);
            case 1 -> tag.putInt(spellSlotCooldown + 1, ticks);
            case 2 -> tag.putInt(spellSlotCooldown + 2, ticks);
            case 3 -> tag.putInt(spellSlotCooldown + 3, ticks);
            case 4 -> tag.putInt(spellSlotCooldown + 4, ticks);
            case 5 -> tag.putInt(spellSlotCooldown + 5, ticks);
        }
        
    }
    private void decrementCoolDowns(ItemStack stack, Level level, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        for (int i = 0; i <= getSpellSlots(this.getLevel()); i++) {
            if (tag.contains(spellSlotCooldown + i)) {
                int coolDown = tag.getInt(spellSlotCooldown + i);
                if (coolDown > 0) {
                    tag.putInt(spellSlotCooldown + i, coolDown - 1);
                }
                if (tag.getInt(spellSlotCooldown + i) - 1 == 0) {
                    level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ALLAY_THROW, player.getSoundSource(), 1.0F, 1.0F);
                }
            }
        }
    }
    
    private boolean isCoolDown(int slot, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(spellSlotCooldown + slot) > 0;
    }
    private int getCoolDown(int slot, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(spellSlotCooldown + slot);
    }
    
    private int getCastProgress(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(remainingDrawTime);
    }
    
    private void setCastProgress(Spell spell, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int ticks = spell.getDrawTime();
        tag.putInt(remainingDrawTime, ticks);
    }
    
    private void resetCastProgress(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(remainingDrawTime, 0);
    }
    private void decrementCastProgress(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int ticks = tag.getInt(remainingDrawTime);
        if (ticks > 0) {
            tag.putInt(remainingDrawTime, ticks - 1);
        }
    }
}
