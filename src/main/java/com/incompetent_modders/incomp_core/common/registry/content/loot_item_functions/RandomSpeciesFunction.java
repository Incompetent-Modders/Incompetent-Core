package com.incompetent_modders.incomp_core.common.registry.content.loot_item_functions;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.data.IncompItemTags;
import com.incompetent_modders.incomp_core.common.registry.ModLootItemFunctions;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

import java.util.List;

@MethodsReturnNonnullByDefault
public class RandomSpeciesFunction extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<RandomSpeciesFunction> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return commonFields(instance).apply(instance, RandomSpeciesFunction::new);
    });
    
    public RandomSpeciesFunction(List<LootItemCondition> lootItemConditions) {
        super(lootItemConditions);
    }
    
    public LootItemFunctionType<RandomSpeciesFunction> getType() {
        return ModLootItemFunctions.RANDOM_SPECIES.get();
    }
    
    public ItemStack run(ItemStack stack, LootContext lootContext) {
        Registry<SpeciesType> speciesRegistry = lootContext.getLevel().registryAccess().registryOrThrow(ModRegistries.Keys.SPECIES_TYPE);
        if (stack.is(IncompItemTags.canAssignSpeciesType)) {
            Holder<SpeciesType> species = speciesRegistry.getRandom(lootContext.getRandom()).orElseThrow();
            stack.set(ModDataComponents.STORED_SPECIES_TYPE.get(), species.getKey());
        }
        return stack;
    }
}
