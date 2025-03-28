package com.incompetent_modders.incomp_core;

import com.incompetent_modders.incomp_core.client.ClientProxy;
import com.incompetent_modders.incomp_core.common.CommonProxy;
import com.incompetent_modders.incomp_core.common.data.IncompDatagen;
import com.incompetent_modders.incomp_core.core.events.IncompetentEventHandler;
import com.incompetent_modders.incomp_core.common.registry.*;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import org.slf4j.Logger;

@SuppressWarnings("removal")
@Mod(IncompCore.MODID)
public class IncompCore
{
    private static IncompCore INSTANCE;
    public static Dist DIST = Dist.DEDICATED_SERVER;
    public static final String MODID = "incompetent_core";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CommonProxy proxy = Util.make(() -> {
        if(FMLLoader.getDist().isClient())
            return new ClientProxy();
        else
            return new CommonProxy();
    });
    
    
    private final IEventBus modEventBus;
    
    public IncompCore(Dist dist, IEventBus modEventBus) {
        this.modEventBus = modEventBus;
        INSTANCE = this;
        new IncompetentEventHandler(modEventBus).register();
        DIST = dist;
        //SetupEvent.EVENT.addListener(IncompCore::setup);
        ModRegistries.register(modEventBus);
        ModAttributes.register(modEventBus);
        ModEffects.register(modEventBus);
        ModSpeciesBehaviourTypes.register(modEventBus);
        ModManaRegenConditions.register(modEventBus);
        ModClassPassiveEffects.register(modEventBus);
        ModAbilities.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModSpellResultTypes.register(modEventBus);
        ModItems.register(modEventBus);
        ModAttachmentTypes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        
        
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        
        if(dist.isClient())
            ClientProxy.modConstruction();
        
        this.modEventBus.addListener(IncompDatagen::gatherDataEvent);
        this.modEventBus.register(this);
    }
    
    //private static void setup(final SetupEvent event) {
    //    SyncHandler.init();
    //}
    @SubscribeEvent
    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
        
        });
        LOGGER.info("HELLO FROM COMMON SETUP");
    }
    
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
    
    public static IEventBus getEventBus() {
        return INSTANCE.modEventBus;
    }
    
    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
