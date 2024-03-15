package com.incompetent_modders.incomp_core.api.network;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.packets.IncompPlayerDataSyncPacket;
import com.incompetent_modders.incomp_core.api.network.packets.SpellSlotScrollPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class IncompNetwork {
    public static SimpleChannel INSTANCE;
    
    private static int packetId = 0;
    
    private static int id() {
        return packetId++;
    }
    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(IncompCore.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        
        INSTANCE = net;
        
        net.messageBuilder(IncompPlayerDataSyncPacket.class, id(), PlayNetworkDirection.PLAY_TO_CLIENT)
                .decoder(IncompPlayerDataSyncPacket::new)
                .encoder(IncompPlayerDataSyncPacket::toBytes)
                .consumerMainThread(IncompPlayerDataSyncPacket::handle)
                .add();
        net.messageBuilder(SpellSlotScrollPacket.class, id(), PlayNetworkDirection.PLAY_TO_SERVER)
                .decoder(SpellSlotScrollPacket::new)
                .encoder(SpellSlotScrollPacket::toBytes)
                .consumerMainThread(SpellSlotScrollPacket::handle)
                .add();
    }
    
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
    
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
    
    public static <MSG> void sendToPlayersTrackingEntity(MSG message, Entity entity) {
        sendToPlayersTrackingEntity(message, entity, false);
    }
    
    public static <MSG> void sendToPlayersTrackingEntity(MSG message, Entity entity, boolean sendToSource) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
        if (sendToSource && entity instanceof ServerPlayer serverPlayer)
            sendToPlayer(message, serverPlayer);
    }
}
