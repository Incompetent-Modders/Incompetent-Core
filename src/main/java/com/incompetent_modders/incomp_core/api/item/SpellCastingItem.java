package com.incompetent_modders.incomp_core.api.item;

import com.incompetent_modders.incomp_core.ClientUtil;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.spell.*;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.nbt.CompoundTag;
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
import java.util.UUID;

public class SpellCastingItem extends Item {
    private final String selSpellSlot_NBT = "selectedSpellSlot";
    private final String spellSlot_NBT = "spellSlot_";
    private final String playerUUID_NBT = "playerUUID";
    private static final String spellSlotCooldown_NBT = "spellSlotCoolDown_";
    private static final String remainingDrawTime_NBT = "castProgress";
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
    private static boolean casting = false;
    public void onUseTick(Level level, LivingEntity entity, ItemStack itemstack, int p_41431_) {
        handleUseTick(level, entity, itemstack);
    }
    
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        CompoundTag tag = itemstack.getOrCreateTag();
        if (player.isShiftKeyDown()) {
            if (SpellUtils.isPreCasting(tag)) {
                return CommonUtils.stopPreCasting(player, itemstack, tag);
            } else {
                if (getSelectedSpell(itemstack) instanceof PreCastSpell<?>) {
                    SpellUtils.setPreCasting(tag, true);
                    SpellUtils.setHasBeenCast(tag, false);
                }
                SpellUtils.setHasBeenCast(tag, false);
            }
        } else {
            if (SpellUtils.playerIsHoldingSpellCatalyst(player, getSelectedSpell(itemstack))) {
                CommonUtils.handleStartUsing(player, hand, itemstack, tag);
            }
            if (!SpellUtils.isPreCasting(tag)) {
                CommonUtils.handleStartUsing(player, hand, itemstack, tag);
            } else {
                CommonUtils.handlePreCasting(player, hand, itemstack, tag);
            }
        }
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
    public void handleUseTick(Level level, LivingEntity entity, ItemStack itemstack) {
        if (!SpellUtils.isPreCasting(itemstack.getOrCreateTag()) && !SpellUtils.hasSpellBeenCast(itemstack.getOrCreateTag())) {
            if (getCastProgress(itemstack) > 0) {
                decrementCastProgress(itemstack);
            }
            CompoundTag tag = itemstack.getOrCreateTag();
            if (getCastProgress(itemstack) == 0 && !casting) {
                Spell spell = getSelectedSpell(itemstack);
                if (spell != null && !isCoolDown(SpellUtils.getSelectedSpellSlot(tag), itemstack) && !casting) {
                    casting = true;
                    if (entity instanceof Player player) {
                        if (spell.hasSpellCatalyst() && SpellUtils.playerIsHoldingSpellCatalyst(player, spell)) {
                            SpellUtils.handleCatalystConsumption(player, spell);
                            spell.cast(level, entity, InteractionHand.MAIN_HAND, false);
                            SpellUtils.setHasBeenCast(tag, true);
                            if (spell instanceof PreCastSpell<?> preCastSpell) {
                                preCastSpell.afterCast(level);
                            }
                            addCoolDown(SpellUtils.getSelectedSpellSlot(tag), getSpellCoolDown(itemstack), itemstack);
                            casting = false;
                        } else if (!spell.hasSpellCatalyst()) {
                            spell.cast(level, entity, InteractionHand.MAIN_HAND, false);
                            SpellUtils.setHasBeenCast(tag, true);
                            if (spell instanceof PreCastSpell<?> preCastSpell) {
                                preCastSpell.afterCast(level);
                            }
                            addCoolDown(SpellUtils.getSelectedSpellSlot(tag), getSpellCoolDown(itemstack), itemstack);
                            casting = false;
                        }
                    }
                }
                entity.stopUsingItem();
            }
        } else {
            casting = false;
        }
    }
    public static String getSpellNameInSlot(CompoundTag tag, int slot) {
        return SpellUtils.deserializeFromSlot(tag, slot).getDisplayName().getString();
    }
    
    
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!SpellUtils.hasSpellBeenCast(tag) && tag.contains(playerUUID_NBT) && level != null) {
            ClientUtil.createSelectedSpellTooltip(tooltip, stack, level, level.getPlayerByUUID(tag.getUUID(playerUUID_NBT)));
            //if (level != null && tag.contains(playerUUID_NBT))
            //    ClientUtil.createCastingInfoTooltip(tooltip, stack, level.getPlayerByUUID(tag.getUUID(playerUUID_NBT)));
        } else if (!SpellUtils.isPreCasting(tag) && SpellUtils.hasSpellBeenCast(tag) && tag.contains(playerUUID_NBT) && level != null) {
            ClientUtil.createSelectedSpellTooltip(tooltip, stack, level, level.getPlayerByUUID(tag.getUUID(playerUUID_NBT)));
            ClientUtil.createAvailableSpellsTooltip(tooltip, stack, this);
        } else {
            Spell spell = getSelectedSpell(stack);
            if (spell instanceof PreCastSpell<?> preCastSpell) {
                ClientUtil.createPreCastTooltip(tooltip, stack, preCastSpell, level);
            }
        }
    }
    
    public void releaseUsing(ItemStack itemstack, Level level, LivingEntity entity, int timeLeft) {
        resetCastProgress(itemstack);
    }
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        CompoundTag tag = stack.getOrCreateTag();
        if (entity instanceof Player player) {
            UUID playerUUID = player.getUUID();
            if (!tag.contains(playerUUID_NBT)) {
                tag.putUUID(playerUUID_NBT, playerUUID);
            } else {
                if (!tag.getUUID(playerUUID_NBT).equals(playerUUID)) {
                    tag.putUUID(playerUUID_NBT, playerUUID);
                }
            }
        }
        if (!(getSelectedSpell(stack) instanceof PreCastSpell<?>)) {
            if (SpellUtils.isPreCasting(tag)) {
                SpellUtils.setPreCasting(tag, false);
            }
        }
        for (int i = 0; i < getSpellSlots(this.getLevel()); i++) {
            if (!tag.contains(spellSlot_NBT + i) || tag.getString(spellSlot_NBT + i).isEmpty()) {
                tag.putString(spellSlot_NBT + i, Spells.EMPTY.get().getSpellIdentifier().toString());
            }
            if (SpellUtils.deserializeFromSlot(tag, i) == null) {
                tag.putString(spellSlot_NBT + i, Spells.EMPTY.get().getSpellIdentifier().toString());
            }
            decrementCoolDowns(stack, level, (Player) entity);
        }
        if (!tag.contains(selSpellSlot_NBT))
            tag.putInt(selSpellSlot_NBT, 0);
    }
    public static Spell getSelectedSpell(ItemStack stack) {
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
    
    public static boolean isCoolDown(int slot, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(spellSlotCooldown_NBT + slot) > 0;
    }
    public static int getCoolDown(int slot, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(spellSlotCooldown_NBT + slot);
    }
    
    public static int getCastProgress(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(remainingDrawTime_NBT);
    }
    
    public static void setCastProgress(Spell spell, ItemStack stack) {
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
    
    protected void updateCaster(ItemStack stack, Level level, LivingEntity entity, int slot) {
        CompoundTag tag = stack.getOrCreateTag();
        Spell spell = SpellUtils.deserializeFromSlot(tag, slot);
        if (spell != null) {
            //Called when a spell slot is changed, should update the casting item's info to reflect the new spell
            resetCastProgress(stack);
            casting = false;
            
        }
    }
}
