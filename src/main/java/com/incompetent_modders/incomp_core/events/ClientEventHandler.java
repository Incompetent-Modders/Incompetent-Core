package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.network.MessageClassAbilitySync;
import com.incompetent_modders.incomp_core.api.network.MessageSpeciesAbilitySync;
import com.incompetent_modders.incomp_core.api.network.MessageSpellSlotScrollSync;
import com.incompetent_modders.incomp_core.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.incompetent_modders.incomp_core.events.ClientModEvents.ACTIVATE_CLASS_ABILITY;
import static com.incompetent_modders.incomp_core.events.ClientModEvents.ACTIVATE_SPECIES_ABILITY;

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
                    var msg = new MessageSpellSlotScrollSync(event.getScrollDeltaY() < 0);
                    PacketDistributor.sendToServer(msg);
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
            PacketDistributor.sendToServer(new MessageClassAbilitySync(true));
        }
        while (ACTIVATE_SPECIES_ABILITY.get().consumeClick()) {
            if (player == null)
                return;
            PacketDistributor.sendToServer(new MessageSpeciesAbilitySync(true));
        }
    }
    
    
}
