package com.incompetent_modders.incomp_core.common.registry.content.loot_item_functions;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.registry.ModItems;
import com.incompetent_modders.incomp_core.common.registry.ModLootItemFunctions;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.incompetent_modders.incomp_core.core.def.ClassType;
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
        Registry<ClassType> classTypeRegistry = lootContext.getLevel().registryAccess().registryOrThrow(ModRegistries.Keys.CLASS_TYPE);
        if (stack.is(ModItems.ASSIGN_CLASS)) {
            Holder<ClassType> classType = classTypeRegistry.getRandom(lootContext.getRandom()).orElseThrow();
            stack.set(ModDataComponents.STORED_CLASS_TYPE.get(), classType.getKey());
        }
        return stack;
    }
}
