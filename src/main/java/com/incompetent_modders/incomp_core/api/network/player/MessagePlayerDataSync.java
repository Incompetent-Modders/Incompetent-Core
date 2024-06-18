package com.incompetent_modders.incomp_core.api.network.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.SyncHandler;
import com.incompetent_modders.incomp_core.api.network.features.MessageDietsSync;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.client.ClientDietManager;
import com.incompetent_modders.incomp_core.client.util.ClientUtil;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static com.incompetent_modders.incomp_core.api.player.PlayerDataCore.PLAYER_DATA_ID;

public record MessagePlayerDataSync(CompoundTag playerData) implements Packet<MessagePlayerDataSync> {
    public static final ClientboundPacketType<MessagePlayerDataSync> TYPE = new MessagePlayerDataSync.Type();
    
    public static void sendToClient(Player player, CompoundTag playerData) {
        SyncHandler.DEFAULT_CHANNEL.sendToPlayer(new MessagePlayerDataSync(playerData), player);
    }
    
    @Override
    public PacketType<MessagePlayerDataSync> type() {
        return TYPE;
    }
    
    private static class Type implements ClientboundPacketType<MessagePlayerDataSync> {
        @Override
        public Class<MessagePlayerDataSync> type() {
            return MessagePlayerDataSync.class;
        }
        
        @Override
        public ResourceLocation id() {
            return IncompCore.makeId("player_data_sync");
        }
        
        public void encode(MessagePlayerDataSync message, RegistryFriendlyByteBuf buf) {
            ExtraByteCodecs.NONNULL_COMPOUND_TAG.encode(message.playerData(), buf);
        }
        
        public MessagePlayerDataSync decode(RegistryFriendlyByteBuf buf) {
            var tag = ExtraByteCodecs.NONNULL_COMPOUND_TAG.decode(buf);
            return new MessagePlayerDataSync(tag);
        }
        
        @Override
        public Runnable handle(MessagePlayerDataSync message) {
            return () -> {
                Player player = ClientUtil.mc().player;
                if (player == null) {
                    IncompCore.LOGGER.warn("Received player data sync packet but player is null");
                    return;
                }
                CompoundTag playerData = message.playerData();
                CompoundTag oldData = player.getPersistentData().getCompound(PLAYER_DATA_ID);
                if (oldData != playerData) {
                    PlayerDataCore.setPlayerData(player, playerData);
                } else {
                    IncompCore.LOGGER.warn("Received player data sync packet with the same data as the current data");
                }
            };
        }
    }
}
