package com.incompetent_modders.incomp_core.api.network.features;

import com.incompetent_modders.incomp_core.api.network.CustomIncompetentPayload;
import com.incompetent_modders.incomp_core.client.ClientDietManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record MessageDietsSync(List<ResourceLocation> dietsIDList) implements CustomPacketPayload {
    public static final Type<MessageDietsSync> TYPE = CustomIncompetentPayload.createType("diet_sync");
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageDietsSync> CODEC = StreamCodec.ofMember(
            MessageDietsSync::write,
            MessageDietsSync::decode);
    
    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeCollection(dietsIDList, ResourceLocation.STREAM_CODEC);
    }
    
    private static MessageDietsSync decode(RegistryFriendlyByteBuf buf) {
        var spells = buf.readList(ResourceLocation.STREAM_CODEC);
        return new MessageDietsSync(spells);
    }
    
    
    public List<ResourceLocation> getDietIDList() {
        return dietsIDList;
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(final MessageDietsSync message, final IPayloadContext ctx)
    {
        ClientDietManager.getInstance().updateDietList(message.getDietIDList());
    }
}
