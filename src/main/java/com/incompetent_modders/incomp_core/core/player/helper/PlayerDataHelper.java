package com.incompetent_modders.incomp_core.core.player.helper;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.registry.ModAttributes;
import com.incompetent_modders.incomp_core.common.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesTypes;
import com.incompetent_modders.incomp_core.common.util.Utils;
import com.incompetent_modders.incomp_core.core.def.ClassType;
import com.incompetent_modders.incomp_core.core.def.Diet;
import com.incompetent_modders.incomp_core.core.def.SpeciesType;
import com.incompetent_modders.incomp_core.core.player.class_type.ClassTypeProvider;
import com.incompetent_modders.incomp_core.core.player.class_type.ClassTypeStorage;
import com.incompetent_modders.incomp_core.core.player.mana.ManaProvider;
import com.incompetent_modders.incomp_core.core.player.species_type.SpeciesTypeProvider;
import com.incompetent_modders.incomp_core.core.player.species_type.SpeciesTypeStorage;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Objects;
import java.util.Optional;

public class PlayerDataHelper {

    public static Optional<ClassTypeProvider> getClassTypeProvider(LivingEntity entity) {
        return Optional.ofNullable(entity.getCapability(ClassTypeProvider.ENTITY_CLASS_TYPE));
    }

    public static Optional<SpeciesTypeProvider> getSpeciesTypeProvider(LivingEntity entity) {
        return Optional.ofNullable(entity.getCapability(SpeciesTypeProvider.ENTITY_SPECIES_TYPE));
    }

    public static Optional<ManaProvider> getManaProvider(LivingEntity entity) {
        return Optional.ofNullable(entity.getCapability(ManaProvider.ENTITY_MANA));
    }

    public static void setClassType(LivingEntity entity, ResourceKey<ClassType> classType) {
        ClassTypeStorage storage = Objects.requireNonNull(getClassTypeProvider(entity).orElse(null)).asStorage();
        getClassTypeProvider(entity).ifPresent(provider -> provider.setStorage(new ClassTypeStorage(classType, storage.cooldownData())));
    }

    public static void setSpeciesType(LivingEntity entity, ResourceKey<SpeciesType> speciesType) {
        SpeciesTypeStorage storage = Objects.requireNonNull(getSpeciesTypeProvider(entity).orElse(null)).asStorage();
        resetSpeciesAttributes(entity, getSpeciesType(entity));
        getSpeciesTypeProvider(entity).ifPresent(provider -> provider.setStorage(new SpeciesTypeStorage(speciesType, storage.cooldownData())));
        addNewSpeciesAttributes(entity, getSpeciesType(entity));
    }

    public static ClassType getClassType(LivingEntity entity) {
        Registry<ClassType> registry = entity.level().registryAccess().registryOrThrow(ModRegistries.Keys.CLASS_TYPE);
        ResourceKey<ClassType> key = getClassTypeProvider(entity).map(provider -> provider.asStorage().classType()).orElse(ModClassTypes.NONE);
        return registry.get(key);
    }

    public static SpeciesType getSpeciesType(LivingEntity entity) {
        Registry<SpeciesType> registry = entity.level().registryAccess().registryOrThrow(ModRegistries.Keys.SPECIES_TYPE);
        ResourceKey<SpeciesType> key = getSpeciesTypeProvider(entity).map(provider -> provider.asStorage().speciesType()).orElse(ModSpeciesTypes.HUMAN);
        return registry.get(key);
    }

    public static Pair<ResourceKey<ClassType>, ClassType> getClassTypeWithKey(LivingEntity entity) {
        Registry<ClassType> registry = entity.level().registryAccess().registryOrThrow(ModRegistries.Keys.CLASS_TYPE);
        ResourceKey<ClassType> key = getClassTypeProvider(entity).map(provider -> provider.asStorage().classType()).orElse(ModClassTypes.NONE);
        ClassType classType = registry.get(key);
        return Pair.of(key, classType);
    }

    public static Pair<ResourceKey<SpeciesType>, SpeciesType> getSpeciesTypeWithKey(LivingEntity entity) {
        Registry<SpeciesType> registry = entity.level().registryAccess().registryOrThrow(ModRegistries.Keys.SPECIES_TYPE);
        ResourceKey<SpeciesType> key = getSpeciesTypeProvider(entity).map(provider -> provider.asStorage().speciesType()).orElse(ModSpeciesTypes.HUMAN);
        SpeciesType speciesType = registry.get(key);
        return Pair.of(key, speciesType);
    }

    public static Diet getDiet(LivingEntity entity) {
        return getSpeciesType(entity).dietType().value();
    }

    public static void setMana(LivingEntity entity, double mana) {
        getManaProvider(entity).ifPresent(provider -> provider.setAmount(mana));
    }

    public static double getMana(LivingEntity entity) {
        return getManaProvider(entity).map(ManaProvider::getAmount).orElse(0.0d);
    }

    public static void addMana(LivingEntity entity, double mana) {
        getManaProvider(entity).ifPresent(provider -> provider.addAmount(mana));
    }

    public static void removeMana(LivingEntity entity, double mana) {
        getManaProvider(entity).ifPresent(provider -> provider.addAmount(-mana));
    }

    public static void setMaxMana(LivingEntity entity, int maxMana) {
        getManaProvider(entity).ifPresent(provider -> provider.setLimit(maxMana));
    }

    public static int getMaxMana(LivingEntity entity) {
        return getManaProvider(entity).map(ManaProvider::getLimit).orElse(0);
    }

    public static void regenerateMana(LivingEntity entity, float modifier) {
        boolean shouldRegen = getClassType(entity).manaRegenCondition().apply(entity.level(), entity);
        double maxMana = getMaxMana(entity);
        double currentMana = getMana(entity);
        AttributeInstance manaRegen = entity.getAttribute(ModAttributes.MANA_REGEN);
        double amountToRegen = manaRegen != null ? manaRegen.getValue() : 0.0d;
        if (shouldRegen && currentMana < maxMana) {
            amountToRegen *= modifier;
            if (currentMana + amountToRegen > maxMana) {
                Utils.onManaHeal(entity, maxMana - currentMana);
                setMana(entity, maxMana);
            } else {
                addMana(entity, amountToRegen);
                Utils.onManaHeal(entity, amountToRegen);
            }

        }
    }


    public static void resetSpeciesAttributes(LivingEntity entity, SpeciesType speciesType) {
        speciesType.attributes().attributeModifiers().forEach((attributeModifierEntry) -> {
                    Holder<Attribute> attribute = attributeModifierEntry.attributeHolder();
                    AttributeModifier modifier = attributeModifierEntry.attributeModifier();
                    AttributeInstance instance = entity.getAttribute(attribute);
                    if (instance != null) {
                        instance.removeModifier(modifier);
                    }
                });
        //RegistryAccess registryAccess = entity.level().registryAccess();
        //Collection<Holder<Attribute>> attributes = registryAccess.registryOrThrow(Registries.ATTRIBUTE).holders().collect(Collectors.toSet());
        //for (Holder<Attribute> attribute : attributes) {
        //    String modifierName = "species_%s_modifier".formatted(attribute.getKey().location().getPath().replace(".", "_"));
        //    if (entity.getAttribute(attribute) == null) {
        //        continue;
        //    }
        //    if (entity.getAttribute(attribute).hasModifier(ResourceLocation.parse(modifierName))) {
        //        entity.getAttribute(attribute).removeModifier(ResourceLocation.parse(modifierName));
        //    }
        //}
    }

    public static void addNewSpeciesAttributes(LivingEntity entity, SpeciesType speciesType) {
        double prevMaxHealthBase = entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
        double prevMaxHealth = entity.getMaxHealth();
        speciesType.attributes().attributeModifiers().forEach((attributeModifierEntry) -> {
            Holder<Attribute> attribute = attributeModifierEntry.attributeHolder();
            AttributeModifier modifier = attributeModifierEntry.attributeModifier();
            AttributeInstance instance = entity.getAttribute(attribute);
            if (attributeModifierEntry.attributeHolder() == Attributes.MAX_HEALTH) {
                boolean multiplyBase = modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
                boolean multiplyTotal = modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
                double newMaxHealth = multiplyBase ? prevMaxHealthBase * modifier.amount() : multiplyTotal ? prevMaxHealth * modifier.amount() : prevMaxHealth + modifier.amount();
                scaleHealth(entity, prevMaxHealth, newMaxHealth);
            }
            if (instance != null) {
                instance.addTransientModifier(modifier);
            }
        });

        PlayerDataHelper.getSpeciesType(entity).attributes().attributes().forEach((attributeEntry) -> {
            Holder<Attribute> attribute = attributeEntry.attributeHolder();
            double baseValue = attributeEntry.baseValue();
            AttributeInstance instance = entity.getAttribute(attribute);
            if (instance != null) {
                instance.setBaseValue(baseValue);
            }
        });
    }

    public static void scaleHealth(LivingEntity entity, double prevMaxHealth, double newMaxHealth) {
        double currentHealth = entity.getHealth();
        double newHealth = (currentHealth / prevMaxHealth) * newMaxHealth;
        entity.setHealth((float) newHealth);
    }
}
