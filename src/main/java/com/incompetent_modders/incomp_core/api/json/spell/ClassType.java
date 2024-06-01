package com.incompetent_modders.incomp_core.api.json.spell;

import com.incompetent_modders.incomp_core.common.util.Utils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record ClassType(ResourceLocation classID, boolean acceptAllClasses) {
    public static final Codec<ClassType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("id", Utils.defaultClass).forGetter(ClassType::classID),
            Codec.BOOL.optionalFieldOf("accept_all_classes", false).forGetter(ClassType::acceptAllClasses)
    ).apply(instance, ClassType::new));
}
