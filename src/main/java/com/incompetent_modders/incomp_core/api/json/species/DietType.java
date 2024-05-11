package com.incompetent_modders.incomp_core.api.json.species;

import com.incompetent_modders.incomp_core.data.IncompItemTags;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public enum DietType {
    VEGETARIAN(IncompItemTags.vegetarianFriendly),
    CARNIVORE(IncompItemTags.carnivoreFriendly),
    OMNIVORE(IncompItemTags.omnivoreFriendly),
    VEGAN(IncompItemTags.veganFriendly)
    ;
    
    private final TagKey<Item> tag;
    
    DietType(TagKey<Item> tag) {
        this.tag = tag;
    }
    
    public TagKey<Item> getTag() {
        return tag;
    }
    
    public static final Codec<DietType> CODEC = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<DietType> read(final DynamicOps<T> ops, final T input) {
            return ops.getStringValue(input).map(value -> {
                for (DietType type : DietType.values()) {
                    if (type.name().equalsIgnoreCase(value)) {
                        return type;
                    }
                }
                return OMNIVORE;
            });
        }
        
        @Override
        public <T> T write(final DynamicOps<T> ops, final DietType value) {
            return ops.createString(value.name());
        }
        
        @Override
        public String toString() {
            return "DietType";
        }
    };
}
