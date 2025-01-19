package com.incompetent_modders.incomp_core.core.def.conditions;

import com.incompetent_modders.incomp_core.api.player.ClassData;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record ConvertCondition(ClassTypeCondition isClass, SpeciesTypeCondition isSpecies, int hasXpLevel, ItemStack isHoldingItem) {
    public static final Codec<ConvertCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ClassTypeCondition.CODEC.optionalFieldOf("class", ClassTypeCondition.ANY).forGetter(ConvertCondition::isClass),
            SpeciesTypeCondition.CODEC.optionalFieldOf("species", SpeciesTypeCondition.ANY).forGetter(ConvertCondition::isSpecies),
            Codec.INT.optionalFieldOf("xp_level", -1).forGetter(ConvertCondition::hasXpLevel),
            ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(ConvertCondition::isHoldingItem)
    ).apply(instance, ConvertCondition::new));

    public static ConvertCondition EMPTY_CONDITION = new ConvertCondition(ClassTypeCondition.ANY, SpeciesTypeCondition.ANY, -1, ItemStack.EMPTY);

    public boolean isClass(Player player) {
        if (isClass().equals(ClassTypeCondition.ANY)) {
            return true;
        }
        return PlayerDataHelper.getClassTypeWithKey(player).getFirst().equals(isClass().classKey());
    }

    public boolean isSpecies(Player player) {
        if (isSpecies().equals(SpeciesTypeCondition.ANY)) {
            return true;
        }
        return PlayerDataHelper.getSpeciesTypeWithKey(player).getFirst().equals(isSpecies().speciesKey());
    }

    public boolean isPlayerLevel(Player player) {
        if (hasXpLevel() == -1) {
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
