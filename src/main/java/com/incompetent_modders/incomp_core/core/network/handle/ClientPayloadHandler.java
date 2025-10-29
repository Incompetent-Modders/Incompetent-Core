package com.incompetent_modders.incomp_core.core.network.handle;

import com.incompetent_modders.incomp_core.IncompClient;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player.ClientSpellData;
import com.incompetent_modders.incomp_core.core.network.clientbound.UpdateClassTypePayload;
import com.incompetent_modders.incomp_core.core.network.clientbound.UpdateManaPayload;
import com.incompetent_modders.incomp_core.core.network.clientbound.UpdateSpeciesTypePayload;
import com.incompetent_modders.incomp_core.core.network.clientbound.UpdateSpellDataPayload;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    private final Minecraft minecraft = Minecraft.getInstance();

    private ClientPayloadHandler() {
    }

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handle(UpdateClassTypePayload payload, IPayloadContext context) {
        PlayerDataHelper.getClassTypeProvider(context.player()).ifPresent(provider -> provider.setStorage(payload.storage()));
    }

    public void handle(UpdateSpeciesTypePayload payload, IPayloadContext context) {
        PlayerDataHelper.getSpeciesTypeProvider(context.player()).ifPresent(provider -> {
            provider.setStorage(payload.storage());
        });
    }

    public void handle(UpdateManaPayload payload, IPayloadContext context) {
        PlayerDataHelper.getManaProvider(context.player()).ifPresent(provider -> {
            provider.setStorage(payload.storage());
        });
    }

    public void handle(UpdateSpellDataPayload payload, IPayloadContext context) {
        IncompClient.setClientSpellData(ClientSpellData.create(payload.spellData()));
    }
}
