package com.incompetent_modders.incomp_core.core.def.attributes.class_type;

import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttribute;
import com.incompetent_modders.incomp_core.api.class_type.attribute.ClassAttributeType;
import com.incompetent_modders.incomp_core.api.species.diet.Diet;
import com.incompetent_modders.incomp_core.common.registry.ModClassAttributes;
import com.incompetent_modders.incomp_core.core.def.attributes.PreventItemUse;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Predicate;

public class ClassPreventItemUse extends ClassAttribute implements PreventItemUse {

    public static final Codec<ClassPreventItemUse> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("items").flatXmap((ingredientList) -> {
                Ingredient[] aingredient = ingredientList.toArray(Ingredient[]::new);
                if (aingredient.length == 0) {
                    return DataResult.error(() -> "No items have been prevented");
                } else {
                    return DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                }
            }, DataResult::success).forGetter(att -> att.items)
    ).apply(instance, ClassPreventItemUse::new));

    public final NonNullList<Ingredient> items;

    public ClassPreventItemUse(NonNullList<Ingredient> items) {
        this.items = items;
    }

    @Override
    public ClassAttributeType<? extends ClassAttribute> getType() {
        return ModClassAttributes.PREVENT_ITEM_USE.get();
    }

    @Override
    public boolean canUseItem(LivingEntity entity, ItemStack stack) {
        Predicate<ItemStack> predicate = (itemStack) ->
                items.stream().anyMatch((ingredient) -> ingredient.test(itemStack));
        return !predicate.test(stack);
    }
}
