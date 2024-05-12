package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.api.item.ClassAssigningItem;
import com.incompetent_modders.incomp_core.api.item.EffectExtendingItem;
import com.incompetent_modders.incomp_core.api.item.SpeciesAssigningItem;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.registry.ModEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class DevItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    
    public static final DeferredHolder<Item, Item> TEST_CASTING_ITEM = ITEMS.register("test_casting_item", () -> new SpellCastingItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> NONE = ITEMS.register("assign_class_none", () -> new ClassAssigningItem(new Item.Properties(), new ResourceLocation(MODID, "none")));
    public static final DeferredHolder<Item, Item> TEST_CLASS = ITEMS.register("assign_class_test_class", () -> new ClassAssigningItem(new Item.Properties(), new ResourceLocation(MODID, "test_class")));
    public static final DeferredHolder<Item, Item> HUMAN = ITEMS.register("assign_species_human", () -> new SpeciesAssigningItem(new Item.Properties(), new ResourceLocation(MODID, "human")));
    public static final DeferredHolder<Item, Item> ZOMBIE = ITEMS.register("assign_species_zombie", () -> new SpeciesAssigningItem(new Item.Properties(), new ResourceLocation(MODID, "zombie")));
    public static final DeferredHolder<Item, Item> ZOMBIE_VIRUS_POSTPONE = ITEMS.register("postpone_effect_zombie_virus", () -> new EffectExtendingItem(ModEffects.ZOMBIE_VIRUS, 1200));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
