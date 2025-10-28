package com.incompetent_modders.incomp_core.api.species.diet;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simple class to modify the values of a FoodProperty.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ModifiableFoodProperties {

    private int nutrition;
    private float saturation;
    private boolean canAlwaysEat;
    private float eatSeconds;
    private Optional<ItemStack> usingConvertsTo;
    private List<FoodProperties.PossibleEffect> effects = new ArrayList<>();

    public ModifiableFoodProperties(FoodProperties inheritFrom) {
        this.nutrition = inheritFrom.nutrition();
        this.saturation = inheritFrom.saturation();
        this.canAlwaysEat = inheritFrom.canAlwaysEat();
        this.eatSeconds = inheritFrom.eatSeconds();
        this.usingConvertsTo = inheritFrom.usingConvertsTo();
        this.effects.addAll(inheritFrom.effects());
    }

    public void setNutrition(int nutrition) {
        this.nutrition = nutrition;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public void canAlwaysEat(boolean canAlwaysEat) {
        this.canAlwaysEat = canAlwaysEat;
    }

    public void setEatSeconds(float eatSeconds) {
        this.eatSeconds = eatSeconds;
    }

    public void setUsingConvert(ItemStack usingConvert) {
        this.usingConvertsTo = Optional.of(usingConvert);
    }

    public void removeUsingConvert() {
        this.usingConvertsTo = Optional.empty();
    }

    public void setEffects(List<FoodProperties.PossibleEffect> effects) {
        this.effects = effects;
    }

    public void addEffect(FoodProperties.PossibleEffect effect) {
        this.effects.add(effect);
    }

    public void removeEffect(Holder<MobEffect> effect) {
        this.effects.removeIf(e -> e.effect().is(effect));
    }

    public FoodProperties toFoodProperties() {
        return new FoodProperties(
                this.nutrition, this.saturation,
                this.canAlwaysEat, this.eatSeconds,
                this.usingConvertsTo, this.effects
        );
    }
}
