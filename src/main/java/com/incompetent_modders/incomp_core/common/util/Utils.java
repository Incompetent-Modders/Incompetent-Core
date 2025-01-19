package com.incompetent_modders.incomp_core.common.util;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.entity.EntitySelectEvent;
import com.incompetent_modders.incomp_core.api.mana.ManaEvent;
import com.incompetent_modders.incomp_core.api.spell.*;
import com.incompetent_modders.incomp_core.common.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.common.registry.ModDiets;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesTypes;
import com.incompetent_modders.incomp_core.core.def.Diet;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

public class Utils {
    
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
    public static ResourceLocation defaultSpecies = ModSpeciesTypes.HUMAN;
    public static ResourceLocation defaultClass = ModClassTypes.NONE;
    public static ResourceKey<Diet> defaultDiet = ModDiets.OMNIVORE;
    public static ResourceLocation location(String path) {
        return IncompCore.makeId(path);
    }
    public static ResourceLocation location(String modId, String path) {
        return ResourceLocation.fromNamespaceAndPath(modId, path);
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
    
    public static String removeExtension(ResourceLocation resourceLocation) {
        String path = resourceLocation.getPath(); // Get the full path from ResourceLocation
        String[] pathElements = path.split("/");
        return pathElements[pathElements.length - 1];
    }
    
    //public static void applyDamage(LivingHurtEvent event, Player player, ItemStack weapon) {
    //    ResourceLocation speciesType = SpeciesData.Get.playerSpecies(player);
    //    SpeciesProperties speciesProperties = SpeciesListener.getSpeciesTypeProperties(speciesType);
    //    if (speciesProperties != null) {
    //        if (speciesProperties.hasEnchantWeaknesses()) {
    //            for (EnchantmentWeaknessProperties properties : speciesProperties.enchantWeaknesses()) {
    //                if (isEnchantedWith(properties.enchantment(), weapon)) {
    //                    float damage = event.getAmount();
    //                    float damageMultiplier = properties.multiplier();
    //                    event.setAmount(damage + getDamageBonus(getEnchantmentLevel(properties.enchantment(), weapon), damageMultiplier));
    //                }
    //            }
    //        }
    //    }
    //}
    //
    //public static float getDamageBonus(int level, float mulBy) {
    //    return  (float)level * mulBy;
    //}
    //public static boolean isEnchantedWith(Holder<Enchantment> enchantment, ItemStack stack) {
    //    return getEnchantmentLevel(enchantment, stack) > 0;
    //}
    //
    //public static int getEnchantmentLevel(Holder<Enchantment> enchantment, ItemStack stack) {
    //    return stack.getEnchantmentLevel(enchantment);
    //}
}
