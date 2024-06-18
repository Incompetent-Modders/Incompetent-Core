package com.incompetent_modders.incomp_core.api.json.species.diet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantmentWeaknessProperties(Holder<Enchantment> enchantment, float multiplier) {
    public static final Codec<EnchantmentWeaknessProperties> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentWeaknessProperties::enchantment),
                Codec.FLOAT.optionalFieldOf("multiplier", 2.5F).forGetter(EnchantmentWeaknessProperties::multiplier)
        ).apply(instance, EnchantmentWeaknessProperties::new);
    });
    
    public static EnchantmentWeaknessProperties of(Holder<Enchantment> enchantment, float multiplier) {
        return new EnchantmentWeaknessProperties(enchantment, multiplier);
    }
}
