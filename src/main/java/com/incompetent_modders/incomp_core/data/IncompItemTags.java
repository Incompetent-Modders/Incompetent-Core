package com.incompetent_modders.incomp_core.data;

import com.incompetent_modders.incomp_core.IncompCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import oshi.util.tuples.Triplet;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

public class IncompItemTags {
    public static final Set<Triplet<TagKey<Item>, Supplier<? extends Item>, String>> ALL_TAGS = new LinkedHashSet<>();
    
    public static TagKey<Item> carnivoreFriendly = modItemTag("carnivore_friendly");
    public static TagKey<Item> veganFriendly = modItemTag("vegan_friendly");
    public static TagKey<Item> omnivoreFriendly = modItemTag("omnivore_friendly");
    public static TagKey<Item> vegetarianFriendly = modItemTag("vegetarian_friendly");
    public static TagKey<Item> neutralFood = modItemTag("neutral_food");
    
    public static TagKey<Item> givesHunger = modItemTag("gives_hunger");
    
    
    public static TagKey<Item> modItemTag(String path) {
        return create(new ResourceLocation(IncompCore.MODID, path));
    }
    public static TagKey<Item> modItemTag(String namespace, String path) {
        return create(new ResourceLocation(namespace, path));
    }
    public static TagKey<Item> forgeItemTag(String path) {
        return create(new ResourceLocation("forge", path));
    }
    
    public static TagKey<Item> create(ResourceLocation name) {
        return TagKey.create(Registries.ITEM, name);
    }
}
