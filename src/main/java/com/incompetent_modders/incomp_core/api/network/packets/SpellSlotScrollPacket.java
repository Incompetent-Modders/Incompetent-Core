package com.incompetent_modders.incomp_core.api.network.packets;

import com.incompetent_modders.incomp_core.api.item.AbstractSpellCastingItem;
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
                if (equipped.getItem() instanceof AbstractSpellCastingItem)
                    ((AbstractSpellCastingItem)equipped.getItem()).changeSelectedSpell(equipped, forward);
            });
        }
    }
}
