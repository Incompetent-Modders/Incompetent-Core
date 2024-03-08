package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.IncompNetwork;
import com.incompetent_modders.incomp_core.api.network.packets.IncompPlayerDataSyncPacket;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = IncompCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START && event.player instanceof ServerPlayer player) {
            //IncompNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new IncompPlayerDataSyncPacket(player));
            var data = PlayerDataCore.getIncompCoreData(player);
            PlayerDataCore.setIncompCoreData(player, data);
            if (PlayerDataCore.getPlayerClassType(player) == null)
                PlayerDataCore.setPlayerClassType(player, ModClassTypes.SIMPLE_HUMAN.get());
        }
    }
}
