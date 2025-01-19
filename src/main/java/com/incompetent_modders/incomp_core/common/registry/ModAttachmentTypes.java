package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.core.player.class_type.ClassTypeStorage;
import com.incompetent_modders.incomp_core.core.player.mana.ManaStorage;
import com.incompetent_modders.incomp_core.core.player.species_type.SpeciesTypeStorage;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, IncompCore.MODID);

    public static final Supplier<AttachmentType<ClassTypeStorage>> CLASS_TYPE = createStorage("class_type", ClassTypeStorage::createDefault, ClassTypeStorage.codec());
    public static final Supplier<AttachmentType<SpeciesTypeStorage>> SPECIES_TYPE = createStorage("species_type", SpeciesTypeStorage::createDefault, SpeciesTypeStorage.codec());
    public static final Supplier<AttachmentType<ManaStorage>> MANA = createStorage("mana", ManaStorage::createDefault, ManaStorage.codec());

    private static <T> Supplier<AttachmentType<T>> createStorage(String name, Supplier<T> defaultValueSupplier, Codec<T> codec) {
        return ATTACHMENT_TYPES.register(name, () -> AttachmentType.builder(defaultValueSupplier).serialize(codec).build());
    }

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
