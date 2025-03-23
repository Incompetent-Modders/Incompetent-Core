package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.item.ItemSpellSlots;
import com.incompetent_modders.incomp_core.core.def.ClassType;
import com.incompetent_modders.incomp_core.core.def.SpeciesType;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, IncompCore.MODID);
    
    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return DATA_COMPONENTS.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REMAINING_DRAW_TIME = register("remaining_draw_time", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<ClassType>>> STORED_CLASS_TYPE = register("stored_class_type", builder -> builder.persistent(ResourceKey.codec(ModRegistries.Keys.CLASS_TYPE)).networkSynchronized(ResourceKey.streamCodec(ModRegistries.Keys.CLASS_TYPE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<SpeciesType>>> STORED_SPECIES_TYPE = register("stored_species_type", builder -> builder.persistent(ResourceKey.codec(ModRegistries.Keys.SPECIES_TYPE)).networkSynchronized(ResourceKey.streamCodec(ModRegistries.Keys.SPECIES_TYPE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<MobEffect>>> STORED_EFFECT_POSTPONE = register("stored_effect_postpone", builder -> builder.persistent(ResourceKey.codec(Registries.MOB_EFFECT)).networkSynchronized(ResourceKey.streamCodec(Registries.MOB_EFFECT)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EFFECT_POSTPONE_DURATION = register("effect_postpone_duration", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_SPELL_SLOTS = register("max_spell_slots", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding().cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SELECTED_SPELL_SLOT = register("selected_spell_slot", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding().cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemSpellSlots>> SPELLS = register("spells", builder -> builder.persistent(ItemSpellSlots.CODEC).networkSynchronized(ItemSpellSlots.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CASTING = register("casting", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).cacheEncoding().cacheEncoding());
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SUMMONED_MAX_USES  = register("summoned_max_uses", builder -> builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SUMMONED_USES = register("summoned_uses", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    
}
