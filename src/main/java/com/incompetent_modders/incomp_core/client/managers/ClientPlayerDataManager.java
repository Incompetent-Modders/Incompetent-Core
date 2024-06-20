package com.incompetent_modders.incomp_core.client.managers;

import com.incompetent_modders.incomp_core.IncompCore;
import net.minecraft.nbt.CompoundTag;

public class ClientPlayerDataManager {
    private static ClientPlayerDataManager instance;
    private CompoundTag playerData;
    
    private ClientPlayerDataManager() {
        playerData = new CompoundTag();
        IncompCore.LOGGER.info("[CLIENT ONLY] Initialized client player data");
    }
    
    public static ClientPlayerDataManager getInstance() {
        if (instance == null) {
            instance = new ClientPlayerDataManager();
        }
        return instance;
    }
    
    public CompoundTag getPlayerData() {
        return playerData;
    }
    
    public void updatePlayerData(CompoundTag updatedData) {
        playerData = updatedData;
        IncompCore.LOGGER.info("[CLIENT ONLY] Updated player data");
    }
}
