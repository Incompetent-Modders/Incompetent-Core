package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.network.IncompNetwork;
import com.incompetent_modders.incomp_core.api.network.packets.SpellSlotScrollPacket;
import com.incompetent_modders.incomp_core.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;

public class ClientEventHandler {
    @SubscribeEvent
    public void onMouseEvent(InputEvent.MouseScrollingEvent event) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null)
            return;
        if(event.getScrollDeltaY() != 0 && ClientUtils.mc().screen == null)
        {
            ItemStack equipped = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(player.isShiftKeyDown())
            {
                if(equipped.getItem() instanceof SpellCastingItem)
                {
                    IncompNetwork.sendToServer(new SpellSlotScrollPacket(event.getScrollDeltaY() < 0));
                    event.setCanceled(true);
                }
            }
        }
    }
    
    
}
