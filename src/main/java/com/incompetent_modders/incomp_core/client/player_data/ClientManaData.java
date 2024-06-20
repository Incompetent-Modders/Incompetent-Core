package com.incompetent_modders.incomp_core.client.player_data;

import com.incompetent_modders.incomp_core.IncompCore;

public class ClientManaData {
    private static ClientManaData instance;
    
    private double mana;
    private double maxMana;
    
    private ClientManaData() {
        IncompCore.LOGGER.info("[CLIENT ONLY] Initialized client mana data");
    }
    
    public static ClientManaData getInstance() {
        if (instance == null) {
            instance = new ClientManaData();
        }
        return instance;
    }
    
    public double getMana() {
        return mana;
    }
    
    public void setMana(double mana) {
        this.mana = mana;
    }
    
    public double getMaxMana() {
        return maxMana;
    }
    
    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
    }
}
