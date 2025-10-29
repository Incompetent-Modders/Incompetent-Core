package com.incompetent_modders.incomp_core.core.player.class_type;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.level.ClassLevelData;
import com.incompetent_modders.incomp_core.common.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.incompetent_modders.incomp_core.core.player.AbilityCooldownData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public record ClassTypeStorage(ResourceKey<ClassType> classType, AbilityCooldownData cooldownData, ClassLevelData levelData) {
    public static final ClassTypeStorage DEFAULT = new ClassTypeStorage(ModClassTypes.NONE, AbilityCooldownData.EMPTY_DATA, ClassLevelData.EMPTY_DATA);

    public static final Codec<ClassTypeStorage> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(ModRegistries.Keys.CLASS_TYPE).fieldOf("class_type").forGetter(ClassTypeStorage::classType),
            AbilityCooldownData.CODEC.optionalFieldOf("cooldown_data", AbilityCooldownData.EMPTY_DATA).forGetter(ClassTypeStorage::cooldownData),
            ClassLevelData.CODEC.optionalFieldOf("level_data", ClassLevelData.EMPTY_DATA).forGetter(ClassTypeStorage::levelData)
    ).apply(instance, ClassTypeStorage::new));

    public static final Codec<ClassTypeStorage> CODEC = Codec.withAlternative(FULL_CODEC,
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceKey.codec(ModRegistries.Keys.CLASS_TYPE).fieldOf("class_type").forGetter(ClassTypeStorage::classType),
                    AbilityCooldownData.CODEC.optionalFieldOf("cooldown_data", AbilityCooldownData.EMPTY_DATA).forGetter(ClassTypeStorage::cooldownData),
                    ClassLevelData.CODEC.optionalFieldOf("level_data", ClassLevelData.EMPTY_DATA).forGetter(ClassTypeStorage::levelData)
            ).apply(instance, ClassTypeStorage::new))
    );

    public static final StreamCodec<FriendlyByteBuf, ClassTypeStorage> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(ModRegistries.Keys.CLASS_TYPE), ClassTypeStorage::classType,
            AbilityCooldownData.STREAM_CODEC, ClassTypeStorage::cooldownData,
            ClassLevelData.STREAM_CODEC, ClassTypeStorage::levelData,
            ClassTypeStorage::new
    );

    public static Codec<ClassTypeStorage> codec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                ResourceKey.codec(ModRegistries.Keys.CLASS_TYPE).fieldOf("class_type").forGetter(storage -> storage.classType),
                AbilityCooldownData.CODEC.optionalFieldOf("cooldown_data", AbilityCooldownData.EMPTY_DATA).forGetter(storage -> storage.cooldownData),
                ClassLevelData.CODEC.optionalFieldOf("level_data", ClassLevelData.EMPTY_DATA).forGetter(storage -> storage.levelData)
        ).apply(instance, ClassTypeStorage::new));
    }

    public static ClassTypeStorage create(ResourceKey<ClassType> classType, AbilityCooldownData cooldownData, ClassLevelData levelData) {
        return new ClassTypeStorage(classType, cooldownData, levelData);
    }

    public static ClassTypeStorage createDefault() {
        return new ClassTypeStorage(ModClassTypes.NONE, AbilityCooldownData.EMPTY_DATA, ClassLevelData.EMPTY_DATA);
    }
}
