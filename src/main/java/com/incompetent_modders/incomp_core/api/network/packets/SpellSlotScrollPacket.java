package com.incompetent_modders.incomp_core.api.network.packets;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.UUID;

public class SpellSlotScrollPacket extends Packet {
    public static final ResourceLocation ID = new ResourceLocation(IncompCore.MODID, "spell_slot_scroll");
    private final boolean forward;
    private final UUID playerUUID;
    private static int selectedSpellSlot;
    public SpellSlotScrollPacket(UUID playerUUID, boolean forward)
    {
        this.forward = forward;
        this.playerUUID = playerUUID;
    }
    
    public SpellSlotScrollPacket(FriendlyByteBuf buf)
    {
        this.forward = buf.readBoolean();
        this.playerUUID = buf.readUUID();
    }
    
    @Override
    public void handleServer(PlayPayloadContext context) {
        Player player = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
        if (player != null) {
            context.workHandler().execute(() -> {
                ItemStack equipped = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (equipped.getItem() instanceof SpellCastingItem)
                    changeSelectedSpell(equipped, forward);
            });
        }
    }
    
    @Override
    public void handleClient(PlayPayloadContext context) {
    
    }
    
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(forward);
    }
    
    @Override
    public ResourceLocation id() {
        return ID;
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
            if (selectedSpellSlot > spellSlots) {
                selectedSpellSlot = 0;
            }
            if (selectedSpellSlot < 0) {
                selectedSpellSlot = 0;
            }
            tag.putInt("selectedSpellSlot", selectedSpellSlot);
        }
    }
}
