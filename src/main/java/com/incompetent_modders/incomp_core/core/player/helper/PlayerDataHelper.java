package com.incompetent_modders.incomp_core.core.player.helper;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesTypes;
import com.incompetent_modders.incomp_core.core.def.ClassType;
import com.incompetent_modders.incomp_core.core.def.SpeciesType;
import com.incompetent_modders.incomp_core.core.player.class_type.ClassTypeProvider;
import com.incompetent_modders.incomp_core.core.player.class_type.ClassTypeStorage;
import com.incompetent_modders.incomp_core.core.player.mana.ManaProvider;
import com.incompetent_modders.incomp_core.core.player.species_type.SpeciesTypeProvider;
import com.incompetent_modders.incomp_core.core.player.species_type.SpeciesTypeStorage;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;

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
        getSpeciesTypeProvider(entity).ifPresent(provider -> provider.setStorage(new SpeciesTypeStorage(speciesType, storage.cooldownData())));
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

    public static void setMana(LivingEntity entity, int mana) {
        getManaProvider(entity).ifPresent(provider -> provider.setAmount(mana));
    }

    public static int getMana(LivingEntity entity) {
        return getManaProvider(entity).map(ManaProvider::getAmount).orElse(0);
    }

    public static void addMana(LivingEntity entity, int mana) {
        getManaProvider(entity).ifPresent(provider -> provider.addAmount(mana));
    }

    public static void removeMana(LivingEntity entity, int mana) {
        getManaProvider(entity).ifPresent(provider -> provider.addAmount(-mana));
    }

    public static void setMaxMana(LivingEntity entity, int maxMana) {
        getManaProvider(entity).ifPresent(provider -> provider.setLimit(maxMana));
    }

    public static int getMaxMana(LivingEntity entity) {
        return getManaProvider(entity).map(ManaProvider::getLimit).orElse(0);
    }
}
