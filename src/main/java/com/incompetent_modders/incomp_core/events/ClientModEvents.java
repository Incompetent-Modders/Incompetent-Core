package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.client.gui.ManaOverlay;
import com.incompetent_modders.incomp_core.client.gui.SpellListOverlay;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

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
    public static final Lazy<KeyMapping> ACTIVATE_CLASS_ABILITY = Lazy.of(() -> new KeyMapping("key.incomp_core.activate_class_ability", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.categories.incomp_core"));
    
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE_CLASS_ABILITY.get());
    }
}
