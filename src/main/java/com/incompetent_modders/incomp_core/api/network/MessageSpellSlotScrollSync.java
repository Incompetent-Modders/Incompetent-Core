package com.incompetent_modders.incomp_core.api.network;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MessageSpellSlotScrollSync(boolean forward) implements CustomPacketPayload {
    
    public static final Type<MessageSpellSlotScrollSync> TYPE = new Type<>(new ResourceLocation(IncompCore.MODID, "spell_slot_scroll"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSpellSlotScrollSync> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            MessageSpellSlotScrollSync::forward,
            MessageSpellSlotScrollSync::new
    );
    public static void handle(final MessageSpellSlotScrollSync message, final IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ItemStack equipped = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (equipped.getItem() instanceof SpellCastingItem) {
                changeSelectedSpell(equipped, message.forward());
            }
        });
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
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
