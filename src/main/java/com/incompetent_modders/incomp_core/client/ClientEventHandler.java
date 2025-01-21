package com.incompetent_modders.incomp_core.client;

import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.core.network.serverbound.ScrollSpellSlotPacket;
import com.incompetent_modders.incomp_core.core.network.serverbound.SpeciesAbilityPayload;
import com.incompetent_modders.incomp_core.client.util.ClientUtil;
import com.incompetent_modders.incomp_core.core.network.serverbound.ClassAbilityPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.incompetent_modders.incomp_core.client.ClientModEvents.ACTIVATE_CLASS_ABILITY;
import static com.incompetent_modders.incomp_core.client.ClientModEvents.ACTIVATE_SPECIES_ABILITY;

public class ClientEventHandler {
    @SubscribeEvent
    public void onMouseEvent(InputEvent.MouseScrollingEvent event) {
        Player player = ClientUtil.mc().player;
        if(event.getScrollDeltaY()!=0&& ClientUtil.mc().screen==null&&player!=null)
        {
            ItemStack equipped = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(player.isShiftKeyDown())
            {
                if(equipped.getItem() instanceof SpellCastingItem)
                {
                    PacketDistributor.sendToServer(new ScrollSpellSlotPacket(event.getScrollDeltaY() < 0));
                    event.setCanceled(true);
                }
            }
        }
    }
    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        while (ACTIVATE_CLASS_ABILITY.get().consumeClick()) {
            if (player == null)
                return;
            PacketDistributor.sendToServer(new ClassAbilityPayload(true));
        }
        while (ACTIVATE_SPECIES_ABILITY.get().consumeClick()) {
            if (player == null)
                return;
            PacketDistributor.sendToServer(new SpeciesAbilityPayload(true));
        }
    }
    
    
}
