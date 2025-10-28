package com.incompetent_modders.incomp_core.core.network.serverbound;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.item.ItemSpellSlots;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.incompetent_modders.incomp_core.core.network.handle.ServerPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ScrollSpellSlotPacket(boolean forward) implements CustomPacketPayload {
    public static final Type<ScrollSpellSlotPacket> TYPE = new Type<>(IncompCore.makeId("scroll_spell_slot"));

    public static final StreamCodec<FriendlyByteBuf, ScrollSpellSlotPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ScrollSpellSlotPacket::forward,
            ScrollSpellSlotPacket::new
    );

    public void handle(IPayloadContext context) {
        ServerPayloadHandler.getInstance().handle(this, context);
    }

    @Override
    public Type<ScrollSpellSlotPacket> type() {
        return TYPE;
    }


    public static void changeSelectedSpell(ItemStack stack, boolean up) {
        if (stack.has(ModDataComponents.SPELLS)) {
            ItemSpellSlots itemSpellSlots = stack.getOrDefault(ModDataComponents.SPELLS, ItemSpellSlots.EMPTY); // Get the component once
            int spellSlots = itemSpellSlots.maxSlots() - 1;
            int selectedSpellSlot = itemSpellSlots.selectedSlot();

            selectedSpellSlot = (up) ? ((selectedSpellSlot == spellSlots) ? 0 : selectedSpellSlot + 1) : ((selectedSpellSlot == 0) ? spellSlots : selectedSpellSlot - 1); // Using ternary operator

            stack.set(ModDataComponents.SPELLS, itemSpellSlots.setSelectedSlot(selectedSpellSlot));
        }
    }
}
