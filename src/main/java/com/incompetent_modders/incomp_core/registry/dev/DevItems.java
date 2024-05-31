package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class DevItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    
    public static final DeferredHolder<Item, Item> TEST_CASTING_ITEM = ITEMS.register("test_casting_item", () -> new SpellCastingItem(new Item.Properties(), 6));
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    
}
