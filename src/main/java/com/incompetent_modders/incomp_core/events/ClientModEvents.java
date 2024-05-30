package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.item.EffectExtendingItem;
import com.incompetent_modders.incomp_core.client.gui.ManaOverlay;
import com.incompetent_modders.incomp_core.client.gui.SpellListOverlay;
import com.incompetent_modders.incomp_core.registry.ModItems;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiConsumer;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;
@EventBusSubscriber(value = Dist.CLIENT, modid = IncompCore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientModEvents {
    static ResourceLocation mana = new ResourceLocation(MODID, "mana");
    static ResourceLocation selected_spell = new ResourceLocation(MODID, "selected_spell");
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.AIR_LEVEL, mana, ManaOverlay.INSTANCE);
        event.registerAbove(VanillaGuiLayers.CHAT, selected_spell, SpellListOverlay.INSTANCE);
    }
    public static final Lazy<KeyMapping> ACTIVATE_CLASS_ABILITY = Lazy.of(() -> new KeyMapping("key.incomp_core.activate_class_ability", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.incomp_core"));
    public static final Lazy<KeyMapping> ACTIVATE_SPECIES_ABILITY = Lazy.of(() -> new KeyMapping("key.incomp_core.activate_species_ability", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.incomp_core"));
    
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE_CLASS_ABILITY.get());
        event.register(ACTIVATE_SPECIES_ABILITY.get());
    }
    
    @SubscribeEvent
    public static void registerColorHandlers(RegisterColorHandlersEvent.Item event) {
        registerItemColours(event::register);
    }
    
    public static void registerItemColours(BiConsumer<ItemColor, ItemLike> register) {
        register.accept((stack, layer) -> {
            return layer > 0 ? -1 : FastColor.ARGB32.opaque(((EffectExtendingItem) stack.getItem()).getColour(stack));
        }, ModItems.EFFECT_POSTPONE.get());
    }
}
