package com.incompetent_modders.incomp_core.core.network.serverbound;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
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

    private static int selectedSpellSlot;

    public static void changeSelectedSpell(ItemStack stack, boolean up) {
        int selectedSpellSlotTag = CastingItemUtil.getSelectedSpellSlot(stack);
        if (stack.has(ModDataComponents.SPELLS) && stack.has(ModDataComponents.MAX_SPELL_SLOTS)) {
            int spellSlots = stack.getOrDefault(ModDataComponents.MAX_SPELL_SLOTS, 6) - 1;
            if (up) {
                if (selectedSpellSlotTag == spellSlots) {
                    selectedSpellSlot = 0;
                } else {
                    selectedSpellSlot++;
                }
            }
            if (!up) {
                if (selectedSpellSlotTag == 0) {
                    selectedSpellSlot = spellSlots;
                } else {
                    selectedSpellSlot--;
                }
            }
            if (selectedSpellSlot > spellSlots) {
                selectedSpellSlot = 0;
            }
            if (selectedSpellSlot < 0) {
                selectedSpellSlot = 0;
            }
            stack.set(ModDataComponents.SELECTED_SPELL_SLOT, selectedSpellSlot);
        }
    }
}
