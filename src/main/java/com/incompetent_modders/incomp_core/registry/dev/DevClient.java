package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.client.model.SpellProjectleModel;
import com.incompetent_modders.incomp_core.api.client.renderer.SpellProjectileRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static com.incompetent_modders.incomp_core.IncompCore.shouldRegisterDevFeatures;

@Mod.EventBusSubscriber(modid = IncompCore.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DevClient {
    
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        if (shouldRegisterDevFeatures()) {
            event.registerEntityRenderer(DevEntities.SPELL_PROJECTILE.get(), SpellProjectileRenderer::new);
        }
    }
    
    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        if (shouldRegisterDevFeatures()) {
            event.registerLayerDefinition(SpellProjectleModel.LAYER_LOCATION, SpellProjectleModel::createBodyLayer);
        }
    }
}
