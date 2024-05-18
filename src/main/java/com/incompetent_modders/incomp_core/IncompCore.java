package com.incompetent_modders.incomp_core;

import com.incompetent_modders.incomp_core.api.network.SyncHandler;
import com.incompetent_modders.incomp_core.data.IncompDatagen;
import com.incompetent_modders.incomp_core.events.ClientEventHandler;
import com.incompetent_modders.incomp_core.events.CommonEventHandler;
import com.incompetent_modders.incomp_core.registry.*;
import com.incompetent_modders.incomp_core.registry.dev.DevItems;
import com.incompetent_modders.incomp_core.util.ModDataComponents;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

@Mod(IncompCore.MODID)
public class IncompCore
{
    private static IncompCore INSTANCE;
    public static final String DISABLE_EXAMPLES_PROPERTY_KEY = "incompetent_core.disable_examples";
    public static final String MODID = "incompetent_core";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    public static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
    public static final ChatFormatting ERROR_FORMAT = ChatFormatting.RED;
    public static final ChatFormatting BUNDLE_CONTENTS_FORMAT = ChatFormatting.DARK_PURPLE;
    private final IEventBus modEventBus;
    public IncompCore(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
        INSTANCE = this;
        
        ModRegistries.register(modEventBus);
        ModAttributes.register(modEventBus);
        ModArgumentTypes.register(modEventBus);
        ModEffects.register(modEventBus);
        ModSpeciesBehaviourTypes.register(modEventBus);
        ModManaRegenConditions.register(modEventBus);
        ModClassPassiveEffects.register(modEventBus);
        ModClassAbilities.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModSpellResultTypes.register(modEventBus);
        
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        
        ClientEventHandler handler = new ClientEventHandler();
        CommonEventHandler commonHandler = new CommonEventHandler();
        NeoForge.EVENT_BUS.register(handler);
        NeoForge.EVENT_BUS.register(commonHandler);
        
        if (shouldRegisterDevFeatures())
        {
            DevItems.register(modEventBus);
        }
        this.modEventBus.addListener(IncompDatagen::gatherDataEvent);
        this.modEventBus.register(this);
    }
    @SubscribeEvent
    private void commonSetup(FMLCommonSetupEvent event)
    {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }
    @SubscribeEvent
    private void onRegisterPayloadHandler(final RegisterPayloadHandlersEvent event)
    {
        SyncHandler.register(event);
    }
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            //ModRegistries.CLASS_TYPE.entrySet().forEach(entry -> {
            //    ClassType classType = entry.getValue();
            //    if (classType.useClassSpecificTexture()) {
            //        LOGGER.info("Class type {} uses class specific Spell Overlay sprites!", entry.getKey());
            //    } else {
            //        LOGGER.info("Class type {} does not use class specific Spell Overlay sprites. Defaulting to fallback sprites!", entry.getKey());
            //    }
            //});
        }
    }
    
    public static boolean shouldRegisterDevFeatures() {
        return !FMLEnvironment.production && !Boolean.getBoolean(DISABLE_EXAMPLES_PROPERTY_KEY);
    }
    public static IEventBus getEventBus() {
        return INSTANCE.modEventBus;
    }
}
