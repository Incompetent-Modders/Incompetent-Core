package com.incompetent_modders.incomp_core.core.def.params;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.network.FriendlyByteBuf;

public enum SpellCategory {
    CURSE,
    RANGED,
    BUFF,
    DEBUFF,
    HEALING,
    SUMMON,
    UTILITY,
    DEFENSE,
    OFFENSE,
    MOBILITY,
    ENVIRONMENTAL
    ;
    
    public static final Codec<SpellCategory> CODEC = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<SpellCategory> read(final DynamicOps<T> ops, final T input) {
            return ops.getStringValue(input).map(value -> {
                for (SpellCategory category : SpellCategory.values()) {
                    if (category.name().equalsIgnoreCase(value)) {
                        return category;
                    }
                }
                return UTILITY;
            });
        }
        
        @Override
        public <T> T write(final DynamicOps<T> ops, final SpellCategory value) {
            return ops.createString(value.name());
        }
        
        @Override
        public String toString() {
            return "SpellCategory";
        }
    };
    
    public static void toNetwork(SpellCategory category, FriendlyByteBuf buf) {
        buf.writeUtf(category.name());
    }
    
    public static SpellCategory fromNetwork(FriendlyByteBuf buf) {
        var category = buf.readUtf();
        for (SpellCategory spellCategory : SpellCategory.values()) {
            if (spellCategory.name().equalsIgnoreCase(category)) {
                return spellCategory;
            } else {
                return UTILITY;
            }
        }
        return UTILITY;
    }
}
