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
import com.teamresourceful.resourcefullib.common.network.Network;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.nio.channels.NetworkChannel;

public class SyncHandler {
    public static final Network DEFAULT_CHANNEL = new Network(IncompCore.makeId("main"), 1);
    private static final String PROTOCOL_VERSION = "1.0.0";
    
    @SubscribeEvent
    public void onLivingTickEvent(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        MessagePlayerDataSync.sendToClient(player, PlayerDataCore.getPlayerData(player));
    }
    
    public static void init() {
        DEFAULT_CHANNEL.register(MessagePlayerDataSync.TYPE);
        
        DEFAULT_CHANNEL.register(MessageSpellsSync.TYPE);
        DEFAULT_CHANNEL.register(MessageSpeciesSync.TYPE);
        DEFAULT_CHANNEL.register(MessageDietsSync.TYPE);
        DEFAULT_CHANNEL.register(MessageClassTypesSync.TYPE);
        
        DEFAULT_CHANNEL.register(MessageClassAbilitySync.TYPE);
        DEFAULT_CHANNEL.register(MessageSpeciesAbilitySync.TYPE);
        
        DEFAULT_CHANNEL.register(MessageSpellSlotScrollSync.TYPE);
    }
}
