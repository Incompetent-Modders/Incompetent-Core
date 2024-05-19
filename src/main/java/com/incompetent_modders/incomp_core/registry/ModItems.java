package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.api.item.ClassAssigningItem;
import com.incompetent_modders.incomp_core.api.item.SpeciesAssigningItem;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    
    public static final DeferredHolder<Item, Item> ASSIGN_CLASS = ITEMS.register("assign_class", () -> new ClassAssigningItem(new Item.Properties(), new ResourceLocation(MODID, "none")));
    public static final DeferredHolder<Item, Item> ASSIGN_SPECIES = ITEMS.register("assign_species", () -> new SpeciesAssigningItem(new Item.Properties(), new ResourceLocation(MODID, "human")));
    public static final DeferredHolder<Item, Item> SPELL_TOME = ITEMS.register("spell_tome", () -> new SpellCastingItem(new Item.Properties(), 1));
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    
    public static Collection<DeferredHolder<Item, ? extends Item>> getItems() {
        return ITEMS.getEntries();
    }
}
