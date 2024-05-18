package com.incompetent_modders.incomp_core.api.json.species.diet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantmentWeaknessProperties(ResourceLocation enchantment, float multiplier) {
    public static final Codec<EnchantmentWeaknessProperties> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                ResourceLocation.CODEC.fieldOf("enchantment").forGetter(EnchantmentWeaknessProperties::enchantment),
                Codec.FLOAT.optionalFieldOf("multiplier", 2.5F).forGetter(EnchantmentWeaknessProperties::multiplier)
        ).apply(instance, EnchantmentWeaknessProperties::new);
    });
    
    public static EnchantmentWeaknessProperties of(ResourceLocation enchantment, float multiplier) {
        return new EnchantmentWeaknessProperties(enchantment, multiplier);
    }
    
    public Enchantment getEnchantment() {
        return BuiltInRegistries.ENCHANTMENT.get(enchantment());
    }
}
