package com.incompetent_modders.incomp_core;

import com.incompetent_modders.incomp_core.api.player.ClientSpellData;

public class IncompClient {
    private static ClientSpellData clientSpellData = ClientSpellData.createDefault();

    public static ClientSpellData getClientSpellData() {
        return clientSpellData;
    }

    public static void setClientSpellData(ClientSpellData data) {
        clientSpellData = data;
    }
}
