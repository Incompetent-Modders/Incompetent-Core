package com.incompetent_modders.incomp_core.core.def.attributes.species;

import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModifyFoodEffectsAttribute extends SpeciesAttribute {

    public static final Codec<ModifyFoodEffectsAttribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.listOf().optionalFieldOf("valid_items", List.of()).forGetter(att -> att.validItems),
            FoodProperties.PossibleEffect.CODEC.listOf().optionalFieldOf("additions", List.of()).forGetter(att -> att.effectAdditions),
            RemovedEffect.LIST_CODEC.optionalFieldOf("removals", List.of()).forGetter(att -> att.effectRemovals)
    ).apply(instance, ModifyFoodEffectsAttribute::new));

    public final List<Ingredient> validItems;
    public final List<FoodProperties.PossibleEffect> effectAdditions;
    public final List<RemovedEffect> effectRemovals;

    public ModifyFoodEffectsAttribute(List<Ingredient> validItems, List<FoodProperties.PossibleEffect> effectAdditions, List<RemovedEffect> effectRemovals) {
        this.validItems = validItems;
        this.effectAdditions = effectAdditions;
        this.effectRemovals = effectRemovals;
    }

    public static Optional<ModifyFoodEffectsAttribute> get(LivingEntity entity) {
        return Optional.ofNullable(PlayerDataHelper.getSpeciesType(entity).get(ModSpeciesAttributes.MODIFY_FOOD_EFFECTS.get()));
    }

    public boolean canApply(ItemStack stack) {
        if (validItems.isEmpty()) return true;
        return validItems.stream().anyMatch(i -> i.test(stack));
    }

    public List<Holder<MobEffect>> removables(RandomSource random) {
        List<Holder<MobEffect>> list = new ArrayList<>();
        for (RemovedEffect removedEffect : effectRemovals) {
            if (removedEffect.shouldRemove(random)) {
                list.add(removedEffect.effect());
            }
        }
        return list;
    }

    @Override
    public SpeciesAttributeType<? extends SpeciesAttribute> getType() {
        return ModSpeciesAttributes.MODIFY_FOOD_EFFECTS.get();
    }

    public record RemovedEffect(Holder<MobEffect> effect, float probability) {
        public static final Codec<RemovedEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(RemovedEffect::effect),
                Codec.FLOAT.optionalFieldOf("probability", 1.0F).forGetter(RemovedEffect::probability)
        ).apply(instance, RemovedEffect::new));

        public static final Codec<List<RemovedEffect>> LIST_CODEC = CODEC.listOf();

        public boolean shouldRemove(RandomSource random) {
            return random.nextFloat() < probability;
        }
    }
}
