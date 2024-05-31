package com.incompetent_modders.incomp_core.api.json.potion.convert;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player.ClassData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record ConvertCondition(ResourceLocation isClassOrSpecies, int hasXpLevel, ItemStack isHoldingItem) {
    public static final ResourceLocation convertToNothing = new ResourceLocation(IncompCore.MODID, "convert_to_nothing");
    public static final Codec<ConvertCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("is_class_or_species", convertToNothing).forGetter(ConvertCondition::isClassOrSpecies),
            Codec.INT.optionalFieldOf("xp_level", 0).forGetter(ConvertCondition::hasXpLevel),
            ItemStack.CODEC.optionalFieldOf("holding_item", ItemStack.EMPTY).forGetter(ConvertCondition::isHoldingItem)
    ).apply(instance, ConvertCondition::new));
    
    public static ConvertCondition EMPTY_CONDITION = new ConvertCondition(convertToNothing, 0, ItemStack.EMPTY);
    
    public boolean isClass(Player player) {
        if (isClassOrSpecies().equals(convertToNothing)) {
            return true;
        }
        return ClassData.Get.playerClassType(player) == isClassOrSpecies();
    }
    
    public boolean isSpecies(Player player) {
        if (isClassOrSpecies().equals(convertToNothing)) {
            return true;
        }
        return ClassData.Get.playerClassType(player) == isClassOrSpecies();
    }
    
    public boolean isPlayerLevel(Player player) {
        if (hasXpLevel() == 0) {
            return true;
        }
        return player.experienceLevel >= hasXpLevel();
    }
    
    public boolean isPlayerHoldingItem(Player player) {
        if (isHoldingItem().isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(player.getMainHandItem().copyWithCount(1), isHoldingItem()) || ItemStack.isSameItemSameComponents(player.getOffhandItem().copyWithCount(1), isHoldingItem());
    }
    
    public boolean shouldConvert(Player player) {
        return (isClass(player) || isSpecies(player)) && isPlayerLevel(player) && isPlayerHoldingItem(player);
    }
}
