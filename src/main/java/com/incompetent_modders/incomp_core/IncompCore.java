package com.incompetent_modders.incomp_core;

import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.network.IncompNetwork;
import com.incompetent_modders.incomp_core.api.spell.Spells;
import com.incompetent_modders.incomp_core.data.IncompDatagen;
import com.incompetent_modders.incomp_core.events.ClientEventHandler;
import com.incompetent_modders.incomp_core.registry.ModArgumentTypes;
import com.incompetent_modders.incomp_core.registry.ModAttributes;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.registry.ModEffects;
import com.incompetent_modders.incomp_core.registry.dev.DevClassTypes;
import com.incompetent_modders.incomp_core.registry.dev.DevItems;
import com.incompetent_modders.incomp_core.registry.dev.DevSpells;
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
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
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
    private final IEventBus modEventBus;
    public IncompCore(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
        INSTANCE = this;
        
        ModRegistries.register(modEventBus);
        ModAttributes.register(modEventBus);
        ModClassTypes.register(modEventBus);
        ModArgumentTypes.register(modEventBus);
        Spells.SPELLS.register(modEventBus);
        ModEffects.register(modEventBus);
        IncompNetwork.init();
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        
        ClientEventHandler handler = new ClientEventHandler();
        NeoForge.EVENT_BUS.register(handler);
        
        if (shouldRegisterDevFeatures())
        {
            DevClassTypes.register(modEventBus);
            DevSpells.register(modEventBus);
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

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            ModRegistries.CLASS_TYPE.entrySet().forEach(entry -> {
                ClassType classType = entry.getValue();
                if (classType.useClassSpecificTexture()) {
                    LOGGER.info("Class type {} uses class specific Spell Overlay sprites!", entry.getKey());
                } else {
                    LOGGER.info("Class type {} does not use class specific Spell Overlay sprites. Defaulting to fallback sprites!", entry.getKey());
                }
            });
        }
    }
    
    public static boolean shouldRegisterDevFeatures() {
        return !FMLEnvironment.production && !Boolean.getBoolean(DISABLE_EXAMPLES_PROPERTY_KEY);
    }
    public static IEventBus getEventBus() {
        return INSTANCE.modEventBus;
    }
}
