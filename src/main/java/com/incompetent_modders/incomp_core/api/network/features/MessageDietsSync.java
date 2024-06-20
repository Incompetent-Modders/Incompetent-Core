package com.incompetent_modders.incomp_core.api.network.features;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.client.managers.ClientDietManager;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record MessageDietsSync(List<ResourceLocation> dietsIDList) implements Packet<MessageDietsSync> {
    public static final ClientboundPacketType<MessageDietsSync> TYPE = new MessageDietsSync.Type();
    
    @Override
    public PacketType<MessageDietsSync> type() {
        return TYPE;
    }
    
    public List<ResourceLocation> getDietIDList() {
        return dietsIDList;
    }
    
    private static class Type implements ClientboundPacketType<MessageDietsSync> {
        
        @Override
        public Class<MessageDietsSync> type() {
            return MessageDietsSync.class;
        }
        
        @Override
        public ResourceLocation id() {
            return IncompCore.makeId("sync_diets");
        }
        
        public void encode(MessageDietsSync message, RegistryFriendlyByteBuf buf) {
            buf.writeCollection(message.dietsIDList, ResourceLocation.STREAM_CODEC);
        }
        
        public MessageDietsSync decode(RegistryFriendlyByteBuf buf) {
            var spells = buf.readList(ResourceLocation.STREAM_CODEC);
            return new MessageDietsSync(spells);
        }
        
        @Override
        public Runnable handle(MessageDietsSync message) {
            return () -> {
                IncompCore.LOGGER.info("Received diet list sync packet");
                ClientDietManager.getInstance().updateDietList(message.getDietIDList());
            };
        }
    }
}
