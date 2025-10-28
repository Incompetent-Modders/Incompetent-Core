package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.incompetent_modders.incomp_core.core.def.attributes.species.*;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

import java.util.List;
import java.util.Map;

import static com.incompetent_modders.incomp_core.core.def.attributes.species.EntityAttributeAttribute.Builder.createAttributeModifier;

public class ModSpeciesTypes {

    public static final ResourceKey<SpeciesType> HUMAN = create("human");
    public static final ResourceKey<SpeciesType> ZOMBIE = create("zombie");

    private static ResourceKey<SpeciesType> create(String name) {
        return ResourceKey.create(ModRegistries.Keys.SPECIES_TYPE, IncompCore.makeId(name));
    }

    private static void registerSpecies(BootstrapContext<SpeciesType> context) {
        var diets = context.lookup(ModRegistries.Keys.DIET);
        register(context, HUMAN, SpeciesType.builder());
        register(context, ZOMBIE, SpeciesType.builder()
                .keepOnDeath(true)
                .addAttributes(
                        new BurnInSunlightAttribute(),
                        new RestrictArmorAttribute(Map.of(EquipmentSlot.CHEST, Ingredient.of(Items.ELYTRA))),
                        new InvertHealAndHarmAttribute(),
                        new DietAttribute(diets.getOrThrow(ModDiets.CARNIVORE)),
                        new ModifyFoodEffectsAttribute(List.of(), List.of(
                                new FoodProperties.PossibleEffect(() -> new MobEffectInstance(MobEffects.CONFUSION, 60), 1.0f)
                        ), List.of(
                                new ModifyFoodEffectsAttribute.RemovedEffect(MobEffects.HUNGER, 1)
                        )),
                        EntityAttributeAttribute.builder()
                                .addModifier(createAttributeModifier(Attributes.ARMOR, 4.0, AttributeModifier.Operation.ADD_VALUE))
                                .addModifier(createAttributeModifier(Attributes.ATTACK_DAMAGE, 3.0, AttributeModifier.Operation.ADD_VALUE, MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemStack.EMPTY.getItem())).build()))
                                .addModifier(createAttributeModifier(Attributes.MOVEMENT_SPEED, 0.075, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                                .addModifier(createAttributeModifier(Attributes.SCALE, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                                .addModifier(createAttributeModifier(Attributes.MAX_HEALTH, 10.0, AttributeModifier.Operation.ADD_VALUE))
                                .addModifier(createAttributeModifier(Attributes.ARMOR_TOUGHNESS, 3.0, AttributeModifier.Operation.ADD_VALUE))
                                .build()
                )
        );
    }

    private static void register(BootstrapContext<SpeciesType> context, ResourceKey<SpeciesType> key, SpeciesType.Builder dietBuilder) {
        context.register(key, dietBuilder.build());
    }

    public static void bootstrap(BootstrapContext<SpeciesType> context) {
        registerSpecies(context);
    }
}
