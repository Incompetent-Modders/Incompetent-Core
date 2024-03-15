package com.incompetent_modders.incomp_core;

import com.incompetent_modders.incomp_core.api.network.IncompNetwork;
import com.incompetent_modders.incomp_core.api.spell.Spells;
import com.incompetent_modders.incomp_core.events.ClientEventHandler;
import com.incompetent_modders.incomp_core.registry.ModArgumentTypes;
import com.incompetent_modders.incomp_core.registry.ModAttributes;
import com.incompetent_modders.incomp_core.registry.ModCapabilities;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(IncompCore.MODID)
public class IncompCore
{
    public static final String MODID = "incompetent_core";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    public static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
    public IncompCore()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        
        
        ModRegistries.register(modEventBus);
        ModAttributes.register(modEventBus);
        ModClassTypes.register(modEventBus);
        ModArgumentTypes.register(modEventBus);
        Spells.SPELLS.register(modEventBus);
        IncompNetwork.register();
        NeoForge.EVENT_BUS.register(this);
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        
        ClientEventHandler handler = new ClientEventHandler();
        NeoForge.EVENT_BUS.register(handler);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
