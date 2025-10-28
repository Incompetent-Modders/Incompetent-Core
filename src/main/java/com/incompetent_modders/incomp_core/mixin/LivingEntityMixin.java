package com.incompetent_modders.incomp_core.mixin;

import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.incompetent_modders.incomp_core.api.species.diet.ModifiableFoodProperties;
import com.incompetent_modders.incomp_core.core.def.attributes.species.ModifyFoodEffectsAttribute;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected abstract void addEatEffect(FoodProperties foodProperties);

    /**
     * @author PouffyDev
     * @reason Modify the food properties
     */
    @Overwrite()
    public ItemStack eat(Level level, ItemStack food, FoodProperties foodProperties) {
        LivingEntity thisAsEntity = (LivingEntity) (Object) this;
        SpeciesType speciesType = PlayerDataHelper.getSpeciesType(thisAsEntity);
        ModifiableFoodProperties modifiableFoodProperties = new ModifiableFoodProperties(foodProperties);
        if (speciesType != null) {
            Optional<ModifyFoodEffectsAttribute> foodEffectsAttribute = ModifyFoodEffectsAttribute.get(thisAsEntity);
            if (foodEffectsAttribute.isPresent() && foodEffectsAttribute.get().canApply(food)) {
                for (Holder<MobEffect> effect : foodEffectsAttribute.get().removables(thisAsEntity.getRandom())) {
                    modifiableFoodProperties.removeEffect(effect);
                }
                for (FoodProperties.PossibleEffect possibleEffect : foodEffectsAttribute.get().effectAdditions) {
                    modifiableFoodProperties.addEffect(possibleEffect);
                }
            }
        }
        level.playSound(null, thisAsEntity.getX(), thisAsEntity.getY(), thisAsEntity.getZ(), thisAsEntity.getEatingSound(food), SoundSource.NEUTRAL, 1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
        addEatEffect(modifiableFoodProperties.toFoodProperties());
        food.consume(1, thisAsEntity);
        thisAsEntity.gameEvent(GameEvent.EAT);
        return food;
    }
}
