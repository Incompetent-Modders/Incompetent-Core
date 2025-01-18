package com.incompetent_modders.incomp_core.core.def.conditions;

import com.incompetent_modders.incomp_core.common.util.Utils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClassTypeCondition(ResourceLocation classID, boolean acceptAllClasses) {
    public static final Codec<ClassTypeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("id", Utils.defaultClass).forGetter(ClassTypeCondition::classID),
            Codec.BOOL.optionalFieldOf("accept_all_classes", false).forGetter(ClassTypeCondition::acceptAllClasses)
    ).apply(instance, ClassTypeCondition::new));
    
    public static final ClassTypeCondition ANY = new ClassTypeCondition(Utils.defaultClass, false);
    public ClassTypeCondition(ResourceLocation classID) {
        this(classID, false);
    }
    
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(classID);
        buf.writeBoolean(acceptAllClasses);
    }
    
    public static ClassTypeCondition decode(RegistryFriendlyByteBuf buf) {
        return fromNetwork(buf);
    }
    
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(classID);
        buf.writeBoolean(acceptAllClasses);
    }
    public static ClassTypeCondition fromNetwork(FriendlyByteBuf buf) {
        var id = buf.readResourceLocation();
        var acceptAll = buf.readBoolean();
        return new ClassTypeCondition(id, acceptAll);
    }
}
