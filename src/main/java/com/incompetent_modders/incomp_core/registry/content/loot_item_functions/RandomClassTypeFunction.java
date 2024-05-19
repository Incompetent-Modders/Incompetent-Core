package com.incompetent_modders.incomp_core.registry.content.loot_item_functions;

import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.registry.ModItems;
import com.incompetent_modders.incomp_core.registry.ModLootItemFunctions;
import com.incompetent_modders.incomp_core.util.ModDataComponents;
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
public class RandomClassTypeFunction extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<RandomClassTypeFunction> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return commonFields(instance).apply(instance, RandomClassTypeFunction::new);
    });
    
    public RandomClassTypeFunction(List<LootItemCondition> lootItemConditions) {
        super(lootItemConditions);
    }
    
    public LootItemFunctionType<RandomClassTypeFunction> getType() {
        return ModLootItemFunctions.RANDOM_CLASS_TYPE.get();
    }
    
    public ItemStack run(ItemStack stack, LootContext lootContext) {
        RandomSource randomsource = lootContext.getRandom();
        List<ResourceLocation> allClassTypes = ClassTypeListener.getAllClassTypes();
        if (stack.is(ModItems.ASSIGN_CLASS)) {
            if (allClassTypes.isEmpty()) {
                LOGGER.error("No class types found in any datapacks");
                return stack;
            }
            ResourceLocation species = allClassTypes.get(randomsource.nextInt(allClassTypes.size()));
            stack.set(ModDataComponents.STORED_CLASS_TYPE.get(), species);
        }
        return stack;
    }
}
