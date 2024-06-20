package com.incompetent_modders.incomp_core.api.network.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.SyncHandler;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.client.managers.ClientPlayerDataManager;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLEnvironment;

public record MessagePlayerDataSync(CompoundTag playerData) implements Packet<MessagePlayerDataSync> {
    public static final ClientboundPacketType<MessagePlayerDataSync> TYPE = new MessagePlayerDataSync.Type();
    
    public static void sendToClient(Player player, CompoundTag playerData) {
        SyncHandler.DEFAULT_CHANNEL.sendToPlayersInLevel(new MessagePlayerDataSync(playerData), player.level());
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
                if (FMLEnvironment.dist.isClient()) {
                    Player player = Minecraft.getInstance().player;
                    if (player == null) {
                        IncompCore.LOGGER.warn("Received player data sync packet but player is null");
                        return;
                    }
                    CompoundTag playerData = message.playerData();
                    CompoundTag oldData = PlayerDataCore.getPlayerData(player);
                    if (oldData != playerData) {
                        ClientPlayerDataManager.getInstance().updatePlayerData(playerData);
                    } else {
                        IncompCore.LOGGER.warn("Received player data sync packet with the same data as the current data");
                    }
                }
            };
        }
    }
}
