package com.incompetent_modders.incomp_core.api.network;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MessagePlayerDataSync(DataAndID dataAndID) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessagePlayerDataSync> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(IncompCore.MODID, "player_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessagePlayerDataSync> CODEC = StreamCodec.composite(
            DataAndID.DATA_AND_ID,
            MessagePlayerDataSync::dataAndID,
            MessagePlayerDataSync::new
    );
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(final MessagePlayerDataSync message, final IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            CompoundTag data = message.dataAndID().data;
            String dataID = message.dataAndID().dataID;
            synchronized(data) {
                PlayerDataCore.setData(player, dataID, data);
            }
        });
    }
    
    public static class DataAndID {
        public CompoundTag data;
        public String dataID;
        
        public DataAndID(CompoundTag data, String dataID) {
            this.data = data;
            this.dataID = dataID;
        }
        
        public CompoundTag getData() {
            return data;
        }
        
        public String getDataID() {
            return dataID;
        }
        
        public static StreamCodec<ByteBuf, DataAndID> DATA_AND_ID = dataAndIDStreamCodec();
        
        static StreamCodec<ByteBuf, DataAndID> dataAndIDStreamCodec() {
            return new StreamCodec<>() {
                public DataAndID decode(ByteBuf byteBuf) {
                    return read(byteBuf);
                }
                
                public void encode(ByteBuf byteBuf, DataAndID dataAndID) {
                    write(byteBuf, dataAndID);
                }
            };
        }
        
        public static DataAndID read(ByteBuf byteBuf) {
            CompoundTag compoundTag = ByteBufCodecs.COMPOUND_TAG.decode(byteBuf);
            String string = ByteBufCodecs.STRING_UTF8.decode(byteBuf);
            return new DataAndID(compoundTag, string);
        }
        
        public static void write(ByteBuf byteBuf, DataAndID dataAndID) {
            ByteBufCodecs.COMPOUND_TAG.encode(byteBuf, dataAndID.data);
            ByteBufCodecs.STRING_UTF8.encode(byteBuf, dataAndID.dataID);
        }
    }
}
