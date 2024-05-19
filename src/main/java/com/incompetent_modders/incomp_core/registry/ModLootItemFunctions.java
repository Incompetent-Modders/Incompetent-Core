package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.registry.content.loot_item_functions.RandomClassTypeFunction;
import com.incompetent_modders.incomp_core.registry.content.loot_item_functions.RandomSpeciesFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootItemFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_TYPES = DeferredRegister.create(BuiltInRegistries.LOOT_FUNCTION_TYPE, IncompCore.MODID);
    
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<RandomSpeciesFunction>> RANDOM_SPECIES = LOOT_FUNCTION_TYPES.register("random_species", () -> new LootItemFunctionType<>(RandomSpeciesFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<RandomClassTypeFunction>> RANDOM_CLASS_TYPE = LOOT_FUNCTION_TYPES.register("random_class_type", () -> new LootItemFunctionType<>(RandomClassTypeFunction.CODEC));
}
