package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.data.IncompItemTags;
import com.incompetent_modders.incomp_core.core.def.Diet;
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
        register(context, VEGAN, Diet.builder().addConsumable(IncompItemTags.veganFriendly));
        register(context, VEGETARIAN, Diet.builder().addConsumable(IncompItemTags.vegetarianFriendly));
        register(context, PESCETARIAN, Diet.builder().addConsumable(IncompItemTags.pescetarianFriendly));
        register(context, OMNIVORE, Diet.builder().addConsumable(IncompItemTags.omnivoreFriendly));
        register(context, CARNIVORE, Diet.builder().addConsumable(IncompItemTags.carnivoreFriendly));
    }

    private static void register(BootstrapContext<Diet> context, ResourceKey<Diet> key, Diet.Builder dietBuilder) {
        context.register(key, dietBuilder.build());
    }

    public static void bootstrap(BootstrapContext<Diet> context) {
        registerDiets(context);
    }
}
