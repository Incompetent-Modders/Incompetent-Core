package com.incompetent_modders.incomp_core.api.network.packets;

import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;

public class SpellSlotScrollPacket {
    private final boolean forward;
    private static int selectedSpellSlot;
    public SpellSlotScrollPacket(boolean forward)
    {
        this.forward = forward;
    }
    
    public SpellSlotScrollPacket(FriendlyByteBuf buf)
    {
        this.forward = buf.readBoolean();
    }
    
    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeBoolean(forward);
    }
    public void handle(NetworkEvent.Context ctx) {
        ServerPlayer serverPlayer = ctx.getSender();
        if (serverPlayer != null) {
            ctx.enqueueWork(() -> {
                ItemStack equipped = serverPlayer.getItemInHand(InteractionHand.MAIN_HAND);
                if (equipped.getItem() instanceof SpellCastingItem)
                    changeSelectedSpell(equipped, forward);
            });
        }
    }
    
    public void changeSelectedSpell(ItemStack stack, boolean up) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return;
        }
        int selectedSpellSlotTag = tag.getInt("selectedSpellSlot");
        if (stack.getItem() instanceof SpellCastingItem item) {
            int spellSlots = SpellCastingItem.getSpellSlots(item.getLevel()) - 1;
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
            tag.putInt("selectedSpellSlot", selectedSpellSlot);
        }
    }
}
