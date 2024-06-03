package com.incompetent_modders.incomp_core.api.network;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.json.species.diet.DietListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.api.network.features.MessageClassTypesSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageDietsSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageSpeciesSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageSpellsSync;
import com.incompetent_modders.incomp_core.api.network.player.MessageClassAbilitySync;
import com.incompetent_modders.incomp_core.api.network.player.MessagePlayerDataSync;
import com.incompetent_modders.incomp_core.api.network.player.MessageSpeciesAbilitySync;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.client.ClientClassTypeManager;
import com.incompetent_modders.incomp_core.client.ClientDietManager;
import com.incompetent_modders.incomp_core.client.ClientSpeciesManager;
import com.incompetent_modders.incomp_core.client.ClientSpellManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
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
        registrar.playToClient(MessagePlayerDataSync.TYPE, MessagePlayerDataSync.CODEC, MessagePlayerDataSync::handle);
        
        registrar.playToClient(MessageSpellsSync.TYPE, MessageSpellsSync.CODEC, MessageSpellsSync::handle);
        registrar.playToClient(MessageSpeciesSync.TYPE, MessageSpeciesSync.CODEC, MessageSpeciesSync::handle);
        registrar.playToClient(MessageClassTypesSync.TYPE, MessageClassTypesSync.CODEC, MessageClassTypesSync::handle);
        registrar.playToClient(MessageDietsSync.TYPE, MessageDietsSync.CODEC, MessageDietsSync::handle);
        
        //registrar.playToClient(MessageSpeciesDataSync.TYPE, MessageSpeciesDataSync.CODEC, MessageSpeciesDataSync::handle);
        
        NeoForge.EVENT_BUS.register(new SyncHandler());
    }
    
    @SubscribeEvent
    public void onLivingTickEvent(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        var msgCD = new MessagePlayerDataSync(PlayerDataCore.getPlayerData(player));
        PacketDistributor.sendToPlayer(player, msgCD);
        
    }
}
