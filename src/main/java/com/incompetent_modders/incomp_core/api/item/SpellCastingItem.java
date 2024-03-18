package com.incompetent_modders.incomp_core.api.item;

import com.incompetent_modders.incomp_core.ClientUtil;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.spell.EmptySpell;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.api.spell.SpellUtils;
import com.incompetent_modders.incomp_core.api.spell.Spells;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
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

public class SpellCastingItem extends Item {
    private final String selSpellSlot_NBT = "selectedSpellSlot";
    private final String spellSlot_NBT = "spellSlot_";
    private final String wielderClassType_NBT = "wielderClassType";
    private final String spellSlotCooldown_NBT = "spellSlotCoolDown_";
    private final String remainingDrawTime_NBT = "castProgress";
    private final int level;
    public SpellCastingItem(Properties properties, int level) {
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
        CompoundTag tag = itemstack.getOrCreateTag();
        if (getCastProgress(itemstack) == 0) {
            Spell spell = getSelectedSpell(itemstack);
            if (spell != null && !isCoolDown(SpellUtils.getSelectedSpellSlot(tag), itemstack)) {
                spell.cast(level, entity, InteractionHand.MAIN_HAND, false);
                level.playSound((Player) entity, entity.getX(), entity.getY(), entity.getZ(), spell.getSpellSound(), entity.getSoundSource(), 1.0F, 1.0F);
                addCoolDown(SpellUtils.getSelectedSpellSlot(tag), getSpellCoolDown(itemstack), itemstack);
            }
            entity.stopUsingItem();
        }
    }
    
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        CompoundTag tag = itemstack.getOrCreateTag();
        if (getCastProgress(itemstack) == 0) {
            setCastProgress(getSelectedSpell(itemstack), itemstack);
        }
        if (isCoolDown(SpellUtils.getSelectedSpellSlot(tag), itemstack))
            return InteractionResultHolder.fail(itemstack);
        if (getSelectedSpell(itemstack) instanceof EmptySpell)
            return InteractionResultHolder.fail(itemstack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }
    public static int getSpellSlots(int tier) {
        return switch (tier) {
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> 5;
            default -> 2;
        };
    }
    
    public static String getSpellNameInSlot(CompoundTag tag, int slot) {
        return SpellUtils.deserializeFromSlot(tag, slot).getDisplayName().getString();
    }
    
    private static final Component SELECTED_SPELL_TITLE = Component.translatable(
                    Util.makeDescriptionId("item", new ResourceLocation(MODID,"spellcasting.selected_spell"))
            ).withStyle(TITLE_FORMAT);
    private static final Component AVAILABLE_SPELLS_TITLE = Component.translatable(
                    Util.makeDescriptionId("item", new ResourceLocation(MODID,"spellcasting.available_spells"))
            ).withStyle(TITLE_FORMAT);
    private static final Component SPELL_INFO_TITLE = Component.translatable(
                    Util.makeDescriptionId("item", new ResourceLocation(MODID,"spellcasting.spell_info"))
            ).withStyle(TITLE_FORMAT);
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        String selectedSlotCoolDown = CommonUtils.timeFromTicks(getCoolDown(SpellUtils.getSelectedSpellSlot(tag), stack), 2);
        tooltip.add(SELECTED_SPELL_TITLE);
        tooltip.add(CommonComponents.space().append(getSelectedSpell(stack).getDisplayName()).withStyle(DESCRIPTION_FORMAT).append(getCoolDown(SpellUtils.getSelectedSpellSlot(tag), stack) > 0 ? " - " + selectedSlotCoolDown : "").append(String.valueOf(" " + SpellUtils.getSelectedSpellSlot(tag))).withStyle(DESCRIPTION_FORMAT));
        tooltip.add(CommonComponents.EMPTY);
        tooltip.add(SPELL_INFO_TITLE);
        tooltip.add(CommonComponents.space().append(Component.translatable("item." + MODID + ".spellcasting.mana_cost").withStyle(TITLE_FORMAT)));
        tooltip.add(CommonComponents.space().append(CommonComponents.space()).append(String.valueOf(getSelectedSpell(stack).getManaCost())).withStyle(DESCRIPTION_FORMAT));
        tooltip.add(CommonComponents.space().append(Component.translatable("item." + MODID + ".spellcasting.cast_time").withStyle(TITLE_FORMAT)));
        tooltip.add(CommonComponents.space().append(CommonComponents.space()).append(CommonUtils.timeFromTicks(getSelectedSpell(stack).getDrawTime(), 1)).withStyle(DESCRIPTION_FORMAT));
        tooltip.add(CommonComponents.EMPTY);
        tooltip.add(AVAILABLE_SPELLS_TITLE);
        for (int i = 0; i < getSpellSlots(this.getLevel()); i++) {
            
            if (i == SpellUtils.getSelectedSpellSlot(tag)) {
                continue;
            }
            String slotCoolDown = CommonUtils.timeFromTicks(getCoolDown(i, stack), 2);
            tooltip.add(CommonComponents.space().append(getSpellNameInSlot(tag, i)).withStyle(DESCRIPTION_FORMAT).append(getCoolDown(i, stack) > 0 ? " - " + slotCoolDown : "").append(String.valueOf(" " + i)).withStyle(DESCRIPTION_FORMAT));
            
        }
        
    }
    
    public void releaseUsing(ItemStack itemstack, Level level, LivingEntity entity, int timeLeft) {
        resetCastProgress(itemstack);
    }
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        CompoundTag tag = stack.getOrCreateTag();
        if (entity instanceof Player player) {
            ClassType classType = PlayerDataCore.getPlayerClassType(player);
            if (classType == null)
                return;
            if (!tag.contains(wielderClassType_NBT)) {
                tag.putString(wielderClassType_NBT, classType.getClassTypeIdentifier().toString());
            } else if (!tag.getString(wielderClassType_NBT).equals(classType.getClassTypeIdentifier().toString())) {
                tag.putString(wielderClassType_NBT, classType.getClassTypeIdentifier().toString());
            }
        }
        
        
        
        for (int i = 0; i < getSpellSlots(this.getLevel()); i++) {
            if (!tag.contains(spellSlot_NBT + i) || tag.getString(spellSlot_NBT + i).isEmpty()) {
                tag.putString(spellSlot_NBT + i, Spells.EMPTY.get().getSpellIdentifier().toString());
            }
            decrementCoolDowns(stack, level, (Player) entity);
            
        }
        if (!tag.contains(selSpellSlot_NBT))
            tag.putInt(selSpellSlot_NBT, 0);
    }
    public ClassType getWielderClassType(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return ModRegistries.CLASS_TYPE.get(new ResourceLocation(tag.getString(wielderClassType_NBT)));
    }
    public Spell getSelectedSpell(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            return SpellUtils.deserializeFromSlot(tag, SpellUtils.getSelectedSpellSlot(tag));
        }
        return Spells.EMPTY.get();
    }
    
    private void addCoolDown(int slot, int ticks, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(spellSlotCooldown_NBT + slot, ticks);
    }
    private void decrementCoolDowns(ItemStack stack, Level level, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        for (int i = 0; i < getSpellSlots(this.getLevel()); i++) {
            if (tag.contains(spellSlotCooldown_NBT + i)) {
                int coolDown = tag.getInt(spellSlotCooldown_NBT + i);
                if (coolDown > 0) {
                    tag.putInt(spellSlotCooldown_NBT + i, coolDown - 1);
                }
                if (tag.getInt(spellSlotCooldown_NBT + i) - 1 == 0) {
                    level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ALLAY_THROW, player.getSoundSource(), 1.0F, 1.0F);
                }
            }
        }
    }
    
    private boolean isCoolDown(int slot, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(spellSlotCooldown_NBT + slot) > 0;
    }
    public int getCoolDown(int slot, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(spellSlotCooldown_NBT + slot);
    }
    
    private int getCastProgress(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(remainingDrawTime_NBT);
    }
    
    private void setCastProgress(Spell spell, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int ticks = spell.getDrawTime();
        tag.putInt(remainingDrawTime_NBT, ticks);
    }
    
    private void resetCastProgress(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(remainingDrawTime_NBT, 0);
    }
    private void decrementCastProgress(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int ticks = tag.getInt(remainingDrawTime_NBT);
        if (ticks > 0) {
            tag.putInt(remainingDrawTime_NBT, ticks - 1);
        }
    }
}
