package com.incompetent_modders.incomp_core.core.def.attributes.species;

import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.api.species.diet.Diet;
import com.incompetent_modders.incomp_core.core.def.attributes.PreventItemUse;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class DietAttribute extends SpeciesAttribute implements PreventItemUse {

    public static final Codec<DietAttribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Diet.CODEC.fieldOf("diet").forGetter(att -> att.diet)
    ).apply(instance, DietAttribute::new));

    public final Holder<Diet> diet;

    public DietAttribute(Holder<Diet> diet) {
        this.diet = diet;
    }

    @Override
    public SpeciesAttributeType<? extends SpeciesAttribute> getType() {
        return ModSpeciesAttributes.DIET.get();
    }

    @Override
    public boolean canUseItem(LivingEntity entity, ItemStack stack) {
        if (!stack.has(DataComponents.FOOD)) return true;
        return diet.value().isEdible(stack);
    }

    @Override
    public Component tooltip(ItemStack itemStack, TooltipFlag flags, Item.TooltipContext context) {
        return Component.translatable("incompetent_core.tooltip.inedible").setStyle(Style.EMPTY.withColor(0xcd0016));
    }
}
