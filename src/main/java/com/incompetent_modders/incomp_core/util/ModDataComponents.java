package com.incompetent_modders.incomp_core.util;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.spell.item.SpellSlot;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, IncompCore.MODID);
    
    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return DATA_COMPONENTS.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REMAINING_DRAW_TIME = register("remaining_draw_time", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> STORED_CLASS_TYPE = register("stored_class_type", builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> STORED_SPECIES_TYPE = register("stored_species_type", builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SpellSlot>> SPELL_SLOT = register("spell_slot", builder -> builder.persistent(SpellSlot.CODEC).networkSynchronized(SpellSlot.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_SPELL_SLOTS = register("max_spell_slots", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding());
    
}
