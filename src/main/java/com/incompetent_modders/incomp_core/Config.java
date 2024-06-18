package com.incompetent_modders.incomp_core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An incompetent_modders config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@EventBusSubscriber(modid = IncompCore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.IntValue MAX_MANA = BUILDER
            .comment("The maximum amount of mana a player can have")
            .defineInRange("maxMana", 100, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
    
    public static int maxMana;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        maxMana = MAX_MANA.get();
    }
}
