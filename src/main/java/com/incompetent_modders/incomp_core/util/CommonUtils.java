package com.incompetent_modders.incomp_core.util;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.entity.EntitySelectEvent;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.mana.ManaEvent;
import com.incompetent_modders.incomp_core.api.spell.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

public class CommonUtils {
    
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
    public static ResourceLocation location(String path) {
        return new ResourceLocation(IncompCore.MODID, path);
    }
    public static ResourceLocation location(String modId, String path) {
        return new ResourceLocation(modId, path);
    }
    
    public static void onCastEvent(Level level, Player player, InteractionHand hand) {
        NeoForge.EVENT_BUS.post(new SpellEvent.CastEvent(level, player, hand));
    }
    
    public static void onEntityCastEvent(Level level, LivingEntity entity, InteractionHand hand) {
        NeoForge.EVENT_BUS.post(new SpellEvent.EntityCastEvent(level, entity, hand));
    }
    
    public static void onSpellSlotScrollEvent(Level level, boolean isScrollingUp, Player player) {
        NeoForge.EVENT_BUS.post(new SpellEvent.SpellSlotScrollEvent(level, isScrollingUp, player));
    }
    public static void onEntitySelectEvent(Level level, Entity target) {
        NeoForge.EVENT_BUS.post(new EntitySelectEvent.SelectEvent(target, level));
    }
    public static void onEntityDeselectEvent(Level level, Entity target) {
        NeoForge.EVENT_BUS.post(new EntitySelectEvent.DeselectEvent(target, level));
    }
    public static float onManaHeal(LivingEntity entity, double amount) {
        ManaEvent event = new ManaEvent(entity, amount);
        return NeoForge.EVENT_BUS.post(event).isCanceled() ? 0 : (float) event.getAmount();
    }
    public static float onManaRegen(LivingEntity entity, double amount) {
        ManaEvent.ManaRegenEvent event = new ManaEvent.ManaRegenEvent(entity, amount);
        return NeoForge.EVENT_BUS.post(event).isCanceled() ? 0 : (float) event.getAmount();
    }
    public static String entityName(Entity entity) {
        if (entity.hasCustomName()) {
            return entity.getCustomName() + " (" + entity.getType().getDescription().toString() + ")";
        }
        return entity.getType().getDescription().getString();
    }
    public static String timeFromTicks(float ticks, int decimalPlaces) {
        float ticks_to_seconds = 20;
        float seconds_to_minutes = 60;
        String affix = "s";
        float time = ticks / ticks_to_seconds;
        if (time > seconds_to_minutes) {
            time /= seconds_to_minutes;
            affix = "m";
        }
        return stringTruncation(time, decimalPlaces) + affix;
    }
    public static String stringTruncation(double f, int decimalPlaces) {
        if (f == Math.floor(f)) {
            return Integer.toString((int) f);
        }
        
        double multiplier = Math.pow(10, decimalPlaces);
        double truncatedValue = Math.floor(f * multiplier) / multiplier;
        
        // Convert the truncated value to a string
        String result = Double.toString(truncatedValue);
        
        // Remove trailing zeros
        result = result.replaceAll("0*$", "");
        
        // Remove the decimal point if there are no decimal places
        result = result.endsWith(".") ? result.substring(0, result.length() - 1) : result;
        
        return result;
    }
    public static float secondsToTicks(float seconds) {
        return seconds * 20.0F;
    }
    
    public static PlayerTeam createTeam(Scoreboard scoreboard, String name, ChatFormatting color) {
        if (scoreboard.getTeamNames().contains(name)) {
            return scoreboard.getPlayerTeam(name);
        } else {
            PlayerTeam team = scoreboard.addPlayerTeam(name);
            team.setDisplayName(Component.literal(name));
            team.setColor(color);
            return team;
        }
    }
    public static void removeTeam(Scoreboard scoreboard, PlayerTeam team) {
        if (scoreboard.getTeamNames().contains(team.getName())) {
            scoreboard.removePlayerTeam(team);
        }
    }
    
    public static InteractionResultHolder<ItemStack> handleStartUsing(Player player, InteractionHand hand, ItemStack itemstack, CompoundTag tag) {
        if (SpellCastingItem.getCastProgress(itemstack) == 0) {
            SpellCastingItem.setCastProgress(SpellCastingItem.getSelectedSpell(itemstack), itemstack);
        }
        if (player.getItemInHand(hand) == itemstack && hand == InteractionHand.OFF_HAND)
            return InteractionResultHolder.fail(itemstack);
        if (SpellCastingItem.isCoolDown(SpellUtils.getSelectedSpellSlot(tag), itemstack))
            return InteractionResultHolder.fail(itemstack);
        if (SpellCastingItem.getSelectedSpell(itemstack) instanceof EmptySpell)
            return InteractionResultHolder.fail(itemstack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }
    public static InteractionResultHolder<ItemStack> handlePreCasting(Player player, InteractionHand hand, ItemStack itemstack, CompoundTag tag) {
        Spell spell = SpellCastingItem.getSelectedSpell(itemstack);
        Level level = player.getCommandSenderWorld();
        if (spell instanceof PreCastSpell<?> preCastSpell) {
            preCastSpell.onPreCast(player.getCommandSenderWorld(), player, hand);
            if (preCastSpell.getSelectedEntities() != null && preCastSpell.getSelectedEntities().size() >= preCastSpell.maxSelections()) {
                preCastSpell.stopPreCast(level);
                SpellUtils.setPreCasting(tag, false);
                SpellUtils.setHasBeenCast(tag, false);
                return InteractionResultHolder.consume(itemstack);
            }
            if (preCastSpell.getSelectedPositions() != null && preCastSpell.getSelectedPositions().size() >= preCastSpell.maxSelections()) {
                preCastSpell.stopPreCast(level);
                SpellUtils.setPreCasting(tag, false);
                SpellUtils.setHasBeenCast(tag, false);
                return InteractionResultHolder.consume(itemstack);
            }
            return InteractionResultHolder.consume(itemstack);
        }
        return InteractionResultHolder.fail(itemstack);
    }
    public static InteractionResultHolder<ItemStack> stopPreCasting(Player player, ItemStack itemstack, CompoundTag tag) {
        if (SpellUtils.isPreCasting(tag)) {
            Spell spell = SpellCastingItem.getSelectedSpell(itemstack);
            if (spell instanceof PreCastSpell<?> preCastSpell) {
                if (preCastSpell.canStopPreCast()) {
                    SpellUtils.setPreCasting(tag, false);
                    SpellUtils.setHasBeenCast(tag, false);
                    return InteractionResultHolder.consume(itemstack);
                } else {
                    SpellUtils.setPreCasting(tag, true);
                    SpellUtils.setHasBeenCast(tag, false);
                    return InteractionResultHolder.consume(itemstack);
                }
            }
        }
        return InteractionResultHolder.fail(itemstack);
    }
    
    public static String removeExtension(ResourceLocation resourceLocation) {
        String path = resourceLocation.getPath(); // Get the full path from ResourceLocation
        String[] pathElements = path.split("/");
        return pathElements[pathElements.length - 1];
    }
}
