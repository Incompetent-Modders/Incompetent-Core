package com.incompetent_modders.incomp_core.api.network;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.action.MessageClassAbilitySync;
import com.incompetent_modders.incomp_core.api.network.action.MessageSpeciesAbilitySync;
import com.incompetent_modders.incomp_core.api.network.action.MessageSpellSlotScrollSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageClassTypesSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageDietsSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageSpeciesSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageSpellsSync;
import com.incompetent_modders.incomp_core.api.network.player.*;
import com.teamresourceful.resourcefullib.common.network.Network;

public class SyncHandler {
    public static final Network DEFAULT_CHANNEL = new Network(IncompCore.makeId("main"), 1);
    
    public static void init() {
        DEFAULT_CHANNEL.register(MessagePlayerDataSync.TYPE);
        
        DEFAULT_CHANNEL.register(MessageSpellsSync.TYPE);
        DEFAULT_CHANNEL.register(MessageSpeciesSync.TYPE);
        DEFAULT_CHANNEL.register(MessageDietsSync.TYPE);
        DEFAULT_CHANNEL.register(MessageClassTypesSync.TYPE);
        DEFAULT_CHANNEL.register(MessageSpeciesAttributesSync.TYPE);
        DEFAULT_CHANNEL.register(MessageManaDataSync.TYPE);
        DEFAULT_CHANNEL.register(MessageSpeciesDataSync.TYPE);
        
        DEFAULT_CHANNEL.register(MessageClassAbilitySync.TYPE);
        DEFAULT_CHANNEL.register(MessageSpeciesAbilitySync.TYPE);
        
        DEFAULT_CHANNEL.register(MessageSpellSlotScrollSync.TYPE);
    }
}
