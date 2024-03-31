package com.incompetent_modders.incomp_core.api.network;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.packets.IncompPlayerDataSyncPacket;
import com.incompetent_modders.incomp_core.api.network.packets.SpellSlotScrollPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import java.lang.reflect.InvocationTargetException;


public class IncompNetwork {
    private static IPayloadRegistrar registrar;
    
    public static void init() {
        IncompCore.getEventBus().addListener(IncompNetwork::registerMessages);
    }
    private static void registerMessages(RegisterPayloadHandlerEvent event) {
        registrar = event.registrar(IncompCore.MODID);
        registerPacket(SpellSlotScrollPacket.class, SpellSlotScrollPacket.ID);
        registerPacket(IncompPlayerDataSyncPacket.class, IncompPlayerDataSyncPacket.ID);
    }
    
    private static <P extends Packet> void registerPacket(Class<P> packet, ResourceLocation id) {
        registrar.play(id, (buffer) -> {
            try {
                return packet.getConstructor(FriendlyByteBuf.class).newInstance(buffer);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }, Packet.IncompPacket::handle);
    }
    
    public static <MSG extends CustomPacketPayload> void send(MSG packet, PacketDistributor.PacketTarget target) {
        target.send(packet);
    }
    
    public static <MSG extends CustomPacketPayload> void sendToServer(MSG packet) {
        send(packet, PacketDistributor.SERVER.noArg());
    }
    
    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG packet, Player player) {
        if (player instanceof ServerPlayer serverPlayer)
            send(packet, PacketDistributor.PLAYER.with(serverPlayer));
    }
}
