package com.incompetent_modders.incomp_core.util;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.mana.ManaEvent;
import com.incompetent_modders.incomp_core.api.spell.SpellEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
    
    public static void onEntityCastEvent(Level level, Entity entity, InteractionHand hand) {
        NeoForge.EVENT_BUS.post(new SpellEvent.EntityCastEvent(level, entity, hand));
    }
    
    public static void onSpellSlotScrollEvent(Level level, boolean isScrollingUp, Player player) {
        NeoForge.EVENT_BUS.post(new SpellEvent.SpellSlotScrollEvent(level, isScrollingUp, player));
    }
    public static float onManaHeal(LivingEntity entity, double amount) {
        ManaEvent event = new ManaEvent(entity, amount);
        return NeoForge.EVENT_BUS.post(event).isCanceled() ? 0 : (float) event.getAmount();
    }
    public static float onManaRegen(LivingEntity entity, double amount) {
        ManaEvent.ManaRegenEvent event = new ManaEvent.ManaRegenEvent(entity, amount);
        return NeoForge.EVENT_BUS.post(event).isCanceled() ? 0 : (float) event.getAmount();
    }
}
