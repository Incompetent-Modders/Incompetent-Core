package com.incompetent_modders.incomp_core.core.player.mana;

import com.incompetent_modders.incomp_core.IncompCore;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

public interface ManaProvider {
    ItemCapability<ManaProvider, Void> ITEM_MANA = ItemCapability.createVoid(IncompCore.makeId("mana_provider"), ManaProvider.class);
    EntityCapability<ManaProvider, Void> ENTITY_MANA = EntityCapability.createVoid(IncompCore.makeId("mana_provider"), ManaProvider.class);

    ManaStorage asStorage();

    void setStorage(ManaStorage storage);

    default int getAmount() {
        return this.asStorage().data().amount();
    }

    default void setAmount(int amount) {
        ManaStorage storage = this.asStorage();

        this.setStorage(new ManaStorage(ManaData.of(Math.min(amount, storage.data().limit()), storage.data().limit()), storage.showInTooltip()));
    }

    default void addAmount(int amount) {
        this.setAmount(this.getAmount() + amount);
    }

    default int getLimit() {
        return this.asStorage().data().limit();
    }

    default void setLimit(int limit) {
        ManaStorage storage = this.asStorage();

        this.setStorage(new ManaStorage(ManaData.of(storage.data().amount(), limit), storage.showInTooltip()));
    }
}
