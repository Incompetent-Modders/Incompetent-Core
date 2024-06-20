package com.incompetent_modders.incomp_core.api.network.action;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.network.SyncHandler;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

public record MessageSpellSlotScrollSync(boolean forward) implements Packet<MessageSpellSlotScrollSync> {
    public static final ServerboundPacketType<MessageSpellSlotScrollSync> TYPE = new MessageSpellSlotScrollSync.Type();
    
    public static void sendToServer(boolean forward) {
        SyncHandler.DEFAULT_CHANNEL.sendToServer(new MessageSpellSlotScrollSync(forward));
    }
    
    @Override
    public PacketType<MessageSpellSlotScrollSync> type() {
        return TYPE;
    }
    
    private static class Type implements ServerboundPacketType<MessageSpellSlotScrollSync> {
        @Override
        public Class<MessageSpellSlotScrollSync> type() {
            return MessageSpellSlotScrollSync.class;
        }
        
        @Override
        public ResourceLocation id() {
            return IncompCore.makeId("spell_slot_scroll");
        }
        
        @Override
        public void encode(MessageSpellSlotScrollSync messageSpellSlotScrollSync, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            registryFriendlyByteBuf.writeBoolean(messageSpellSlotScrollSync.forward());
        }
        
        @Override
        public MessageSpellSlotScrollSync decode(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            var forward = registryFriendlyByteBuf.readBoolean();
            return new MessageSpellSlotScrollSync(forward);
        }
        @Override
        public Consumer<Player> handle(MessageSpellSlotScrollSync messageSpellSlotScrollSync) {
            return (player) -> {
                ItemStack equipped = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (equipped.getItem() instanceof SpellCastingItem) {
                    changeSelectedSpell(equipped, messageSpellSlotScrollSync.forward());
                }
            };
        }
    }
    private static int selectedSpellSlot;
    
    public static void changeSelectedSpell(ItemStack stack, boolean up) {
        int selectedSpellSlotTag = CastingItemUtil.getSelectedSpellSlot(stack);
        if (stack.getItem() instanceof SpellCastingItem) {
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
            CustomData.update(DataComponents.CUSTOM_DATA, stack, (tag) -> {
                tag.putInt("selectedSpellSlot", selectedSpellSlot);
            });
            
        }
    }
}
