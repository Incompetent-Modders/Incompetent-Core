package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.client.gui.ManaOverlay;
import com.incompetent_modders.incomp_core.client.gui.SpellListOverlay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = IncompCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {
    static ResourceLocation mana = new ResourceLocation(MODID, "mana");
    static ResourceLocation selected_spell = new ResourceLocation(MODID, "selected_spell");
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        
        event.registerAbove(VanillaGuiOverlay.AIR_LEVEL.id(), mana, ManaOverlay.INSTANCE);
        event.registerAbove(VanillaGuiOverlay.CHAT_PANEL.id(), selected_spell, SpellListOverlay.INSTANCE);
    }
}
