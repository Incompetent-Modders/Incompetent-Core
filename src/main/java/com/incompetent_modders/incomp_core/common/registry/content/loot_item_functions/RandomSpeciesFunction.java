package com.incompetent_modders.incomp_core.common.registry.content.loot_item_functions;

import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.common.registry.ModItems;
import com.incompetent_modders.incomp_core.common.registry.ModLootItemFunctions;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
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
        RandomSource randomsource = lootContext.getRandom();
        List<ResourceLocation> allSpecies = SpeciesListener.getAllSpecies();
        if (stack.is(ModItems.ASSIGN_SPECIES)) {
            if (allSpecies.isEmpty()) {
                LOGGER.error("No species found in any datapacks");
                return stack;
            }
            ResourceLocation species = allSpecies.get(randomsource.nextInt(allSpecies.size()));
            stack.set(ModDataComponents.STORED_SPECIES_TYPE.get(), species);
        }
        return stack;
    }
}
