package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.data.IncompItemTags;
import com.incompetent_modders.incomp_core.api.species.diet.Diet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class ModDiets {

    public static final ResourceKey<Diet> VEGAN = create("vegan");
    public static final ResourceKey<Diet> VEGETARIAN = create("vegetarian");
    public static final ResourceKey<Diet> PESCETARIAN = create("pescetarian");
    public static final ResourceKey<Diet> OMNIVORE = create("omnivore");
    public static final ResourceKey<Diet> CARNIVORE = create("carnivore");

    private static ResourceKey<Diet> create(String name) {
        return ResourceKey.create(ModRegistries.Keys.DIET, IncompCore.makeId(name));
    }

    private static void registerDiets(BootstrapContext<Diet> context) {
        Diet.Builder veganBuilder = Diet.builder().addConsumable(IncompItemTags.veganFriendly);
        Diet.Builder vegetarianBuilder = Diet.builder().inheritFrom(veganBuilder).addConsumable(IncompItemTags.vegetarianFriendly);
        Diet.Builder pescetarianBuilder = Diet.builder().inheritFrom(vegetarianBuilder).addConsumable(IncompItemTags.pescetarianFriendly);
        Diet.Builder carnivoreBuilder = Diet.builder().addConsumable(IncompItemTags.carnivoreFriendly);
        Diet.Builder omnivoreBuilder = Diet.builder().inheritFrom(pescetarianBuilder).inheritFrom(carnivoreBuilder).addConsumable(IncompItemTags.omnivoreFriendly);
        register(context, VEGAN, veganBuilder);
        register(context, VEGETARIAN, vegetarianBuilder);
        register(context, PESCETARIAN, pescetarianBuilder);
        register(context, CARNIVORE, carnivoreBuilder);
        register(context, OMNIVORE, omnivoreBuilder);
    }

    private static void register(BootstrapContext<Diet> context, ResourceKey<Diet> key, Diet.Builder dietBuilder) {
        context.register(key, dietBuilder.build());
    }

    public static void bootstrap(BootstrapContext<Diet> context) {
        registerDiets(context);
    }
}
