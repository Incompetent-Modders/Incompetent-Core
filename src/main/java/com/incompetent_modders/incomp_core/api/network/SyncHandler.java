package com.incompetent_modders.incomp_core.api.network;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class SyncHandler {
    private static final String PROTOCOL_VERSION = "1.0.0";
    
    public static void register(final RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar(IncompCore.MODID)
                .versioned(PROTOCOL_VERSION)
                .optional();
        
        //TO SERVER
        registrar.playToServer(MessageSpellSlotScrollSync.TYPE, MessageSpellSlotScrollSync.CODEC, MessageSpellSlotScrollSync::handle);
        registrar.playToServer(MessageClassAbilitySync.TYPE, MessageClassAbilitySync.CODEC, MessageClassAbilitySync::handle);
        registrar.playToServer(MessageSpeciesAbilitySync.TYPE, MessageSpeciesAbilitySync.CODEC, MessageSpeciesAbilitySync::handle);
        
        //TO CLIENT
        registrar.playToClient(MessageClassDataSync.TYPE, MessageClassDataSync.CODEC, MessageClassDataSync::handle);
        registrar.playToClient(MessageSpeciesDataSync.TYPE, MessageSpeciesDataSync.CODEC, MessageSpeciesDataSync::handle);
        registrar.playToClient(MessageManaDataSync.TYPE, MessageManaDataSync.CODEC, MessageManaDataSync::handle);
        
        NeoForge.EVENT_BUS.register(new SyncHandler());
    }
    
    @SubscribeEvent
    public void onLivingTickEvent(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        
        var msgCD = new MessageClassDataSync(PlayerDataCore.getClassData(player));
        var msgSD = new MessageSpeciesDataSync(PlayerDataCore.getSpeciesData(player));
        var msgMD = new MessageManaDataSync(PlayerDataCore.getManaData(player));
        PacketDistributor.sendToPlayer(player, msgCD);
        PacketDistributor.sendToPlayer(player, msgMD);
        PacketDistributor.sendToPlayer(player, msgSD);
    }
}
