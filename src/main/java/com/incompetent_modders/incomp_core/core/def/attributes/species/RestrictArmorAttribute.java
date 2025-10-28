package com.incompetent_modders.incomp_core.core.def.attributes.species;

import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.core.def.attributes.PreventEquip;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

public class RestrictArmorAttribute extends SpeciesAttribute implements PreventEquip {

    public static final Codec<Map<EquipmentSlot, Ingredient>> RESTRICTION_CODEC = Codec.unboundedMap(
            EquipmentSlot.CODEC, Ingredient.CODEC_NONEMPTY);

    public static final Codec<RestrictArmorAttribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RESTRICTION_CODEC.fieldOf("restrictions").forGetter(att -> att.restrictions)
    ).apply(instance, RestrictArmorAttribute::new));

    public final Map<EquipmentSlot, Ingredient> restrictions;

    public RestrictArmorAttribute(Map<EquipmentSlot, Ingredient> restrictions) {
        this.restrictions = restrictions;
    }

    public boolean canEquip(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot) {
        if (restrictions.get(slot) == null) return true;
        return !restrictions.get(slot).test(itemStack);
    }

    @Override
    public SpeciesAttributeType<? extends SpeciesAttribute> getType() {
        return ModSpeciesAttributes.RESTRICT_ARMOR.get();
    }
}
