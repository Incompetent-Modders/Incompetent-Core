package com.incompetent_modders.incomp_core.core.def.params;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.core.def.ClassType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;

public record ClassTypeCondition(ResourceKey<ClassType> classKey, boolean acceptAllClasses) {
    public static final Codec<ClassTypeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(ModRegistries.Keys.CLASS_TYPE).optionalFieldOf("class", ModClassTypes.NONE).forGetter(ClassTypeCondition::classKey),
            Codec.BOOL.optionalFieldOf("accept_all_classes", true).forGetter(ClassTypeCondition::acceptAllClasses)
    ).apply(instance, ClassTypeCondition::new));
    
    public static final ClassTypeCondition ANY = new ClassTypeCondition(ModClassTypes.NONE, true);
    public ClassTypeCondition(ResourceKey<ClassType> classKey) {
        this(classKey, false);
    }
    
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeResourceKey(classKey);
        buf.writeBoolean(acceptAllClasses);
    }
    
    public static ClassTypeCondition decode(RegistryFriendlyByteBuf buf) {
        return fromNetwork(buf);
    }
    
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceKey(classKey);
        buf.writeBoolean(acceptAllClasses);
    }
    public static ClassTypeCondition fromNetwork(FriendlyByteBuf buf) {
        var key = buf.readResourceKey(ModRegistries.Keys.CLASS_TYPE);
        var acceptAll = buf.readBoolean();
        return new ClassTypeCondition(key, acceptAll);
    }
}
