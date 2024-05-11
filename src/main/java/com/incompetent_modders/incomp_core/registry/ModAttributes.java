package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

@EventBusSubscriber(modid = IncompCore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModAttributes {
    public static final HashMap<DeferredHolder<Attribute, Attribute>, UUID> UUIDS = new HashMap<>();
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, IncompCore.MODID);
    
    public static final DeferredHolder<Attribute, Attribute> MANA_REGEN = registerAttribute("mana_regen", (id) -> new RangedAttribute(id, 0.5D, 0.0D, 2000.0D).setSyncable(true), "ed69a04a-eb94-4828-88fc-dd366145ed46");
    
    public static final DeferredHolder<Attribute, Attribute> MAX_MANA = registerAttribute("max_mana", (id) -> new RangedAttribute(id, 100.0D, 0.0D, 1000000000.0D).setSyncable(true), "6393df79-d450-4374-9826-b81c2db0f053");
    
    @Deprecated
    public static final DeferredHolder<Attribute, Attribute> MAX_MANA_BONUS = MAX_MANA, FLAT_MANA_BONUS = MAX_MANA;
    
    public static DeferredHolder<Attribute, Attribute> registerAttribute(String name, Function<String, Attribute> attribute, String uuid) {
        return registerAttribute(name, attribute, UUID.fromString(uuid));
    }
    
    public static DeferredHolder<Attribute, Attribute> registerAttribute(String name, Function<String, Attribute> attribute, UUID uuid) {
        DeferredHolder<Attribute, Attribute> registryObject = ATTRIBUTES.register(name, () -> attribute.apply(name));
        UUIDS.put(registryObject, uuid);
        return registryObject;
    }
    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent e) {
        e.getTypes().forEach(entity -> {
            e.add(entity, MAX_MANA);
            e.add(entity, MANA_REGEN);
        });
    }
    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
