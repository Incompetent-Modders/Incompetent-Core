package com.incompetent_modders.incomp_core.common.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModLevelBasedValues {
    public static final DeferredRegister<MapCodec<? extends LevelBasedValue>> LEVEL_BASED_VALUES = DeferredRegister.create(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE, MODID);

    public static final DeferredHolder<MapCodec<? extends LevelBasedValue>, MapCodec<Sequential>> SEQUENTIAL = LEVEL_BASED_VALUES.register("sequential", () -> Sequential.CODEC);

    public static void register(IEventBus eventBus) {
        LEVEL_BASED_VALUES.register(eventBus);
    }

    static Sequential sequential(float base, float multiplier) {
        return new Sequential(base, multiplier);
    }

    public record Sequential(float base, float multiplier) implements LevelBasedValue {
        @Override
        public float calculate(int level) {
            float effectiveValue = this.base;
            int lvl = level - 1;
            while (lvl > 0) {
                effectiveValue = Math.round(effectiveValue * this.multiplier);
                lvl -= 1;
            }
            return effectiveValue;
        }

        public static final MapCodec<Sequential> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.FLOAT.fieldOf("base").forGetter(Sequential::base),
                Codec.FLOAT.fieldOf("multiplier").forGetter(Sequential::multiplier)
        ).apply(instance, Sequential::new));

        @Override
        public MapCodec<? extends LevelBasedValue> codec() {
            return CODEC;
        }
    }
}
