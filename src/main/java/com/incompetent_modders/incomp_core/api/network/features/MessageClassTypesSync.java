package com.incompetent_modders.incomp_core.api.network.features;

import com.incompetent_modders.incomp_core.api.network.CustomIncompetentPayload;
import com.incompetent_modders.incomp_core.client.ClientClassTypeManager;
import com.incompetent_modders.incomp_core.client.ClientSpeciesManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record MessageClassTypesSync(List<ResourceLocation> classTypesIDList) implements CustomPacketPayload {
    public static final Type<MessageClassTypesSync> TYPE = CustomIncompetentPayload.createType("class_type_sync");
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageClassTypesSync> CODEC = StreamCodec.ofMember(
            MessageClassTypesSync::write,
            MessageClassTypesSync::decode);
    
    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeCollection(classTypesIDList, ResourceLocation.STREAM_CODEC);
    }
    
    private static MessageClassTypesSync decode(RegistryFriendlyByteBuf buf) {
        var spells = buf.readList(ResourceLocation.STREAM_CODEC);
        return new MessageClassTypesSync(spells);
    }
    
    
    public List<ResourceLocation> getClassTypeIDList() {
        return classTypesIDList;
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(final MessageClassTypesSync message, final IPayloadContext ctx)
    {
        ClientClassTypeManager.getInstance().updateClassTypeList(message.getClassTypeIDList());
    }
}
