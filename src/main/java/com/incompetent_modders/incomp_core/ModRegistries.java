package com.incompetent_modders.incomp_core;

import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ModRegistries {
    public static final ResourceKey<Registry<Spell>> SPELLS_KEY = ResourceKey.createRegistryKey(new ResourceLocation(IncompCore.MODID, "spells"));
    public static final ResourceKey<Registry<ClassType>> CLASS_TYPE_KEY = ResourceKey.createRegistryKey(new ResourceLocation(IncompCore.MODID, "class_type"));
    public static final Registry<Spell> SPELL = new RegistryBuilder<>(SPELLS_KEY).create();
    public static final Registry<ClassType> CLASS_TYPE = new RegistryBuilder<>(CLASS_TYPE_KEY).create();
    
    public static void register(IEventBus bus) {
        bus.addListener(NewRegistryEvent.class, event -> event.register(SPELL));
        bus.addListener(NewRegistryEvent.class, event -> event.register(CLASS_TYPE));
    }
}
