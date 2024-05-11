package com.incompetent_modders.incomp_core.devtest.spell_results;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public enum RaycastType {
    HIT_BLOCK(BlockHitResult.class),
    HIT_BLOCK_NO_WATER(BlockHitResult.class),
    HIT_ENTITY(EntityHitResult.class)
    ;
    
    private final Class<? extends HitResult> hitResultClass;
    
    RaycastType(Class<? extends HitResult> hitResultClass) {
        this.hitResultClass = hitResultClass;
    }
    
    public Class<? extends HitResult> getHitResultClass() {
        return hitResultClass;
    }
    
    public static final Codec<RaycastType> CODEC = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<RaycastType> read(final DynamicOps<T> ops, final T input) {
            return ops.getStringValue(input).map(value -> {
                for (RaycastType type : RaycastType.values()) {
                    if (type.name().equalsIgnoreCase(value)) {
                        return type;
                    }
                }
                return HIT_BLOCK;
            });
        }
        
        @Override
        public <T> T write(final DynamicOps<T> ops, final RaycastType value) {
            return ops.createString(value.name());
        }
        
        @Override
        public String toString() {
            return "RaycastType";
        }
    };
}
