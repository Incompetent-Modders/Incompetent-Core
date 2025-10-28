package com.incompetent_modders.incomp_core.client;

import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.incompetent_modders.incomp_core.client.util.KeyState;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.core.def.attributes.PreventEquip;
import com.incompetent_modders.incomp_core.core.def.attributes.species.BurnInLightAttribute;
import com.incompetent_modders.incomp_core.core.def.attributes.PreventItemUse;
import com.incompetent_modders.incomp_core.core.def.attributes.species.RestrictArmorAttribute;
import com.incompetent_modders.incomp_core.core.network.serverbound.ScrollSpellSlotPacket;
import com.incompetent_modders.incomp_core.core.network.serverbound.SpeciesAbilityPayload;
import com.incompetent_modders.incomp_core.client.util.ClientUtil;
import com.incompetent_modders.incomp_core.core.network.serverbound.ClassAbilityPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

import static com.incompetent_modders.incomp_core.client.ClientModEvents.ACTIVATE_CLASS_ABILITY;
import static com.incompetent_modders.incomp_core.client.ClientModEvents.ACTIVATE_SPECIES_ABILITY;
import static com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper.getClassType;
import static com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper.getSpeciesType;

public class ClientEventHandler {

    @SubscribeEvent
    public void onMouseEvent(InputEvent.MouseScrollingEvent event) {
        Player player = ClientUtil.mc().player;
        if(event.getScrollDeltaY()!=0&& ClientUtil.mc().screen==null&&player!=null) {
            ItemStack equipped = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(player.isShiftKeyDown()) {
                if(equipped.has(ModDataComponents.SPELLS)) {
                    PacketDistributor.sendToServer(new ScrollSpellSlotPacket(event.getScrollDeltaY() < 0));
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        if (player != null) {
            SpeciesType speciesType = getSpeciesType(player);
            ClassType classType = getClassType(player);
            // Item Use Preventions from the species and class
            List<PreventItemUse> preventItemUses = new ArrayList<>();
            preventItemUses.addAll(speciesType.getOfType(PreventItemUse.class));
            preventItemUses.addAll(classType.getOfType(PreventItemUse.class));
            for (PreventItemUse preventItemUse : preventItemUses) {
                if (!preventItemUse.canUseItem(player, stack)) {
                    event.getToolTip().add(preventItemUse.tooltip(stack, event.getFlags(), event.getContext()));
                }
            }
            // Equipment Preventions from the species and class
            List<PreventEquip> preventEquips = new ArrayList<>();
            preventEquips.addAll(speciesType.getOfType(PreventEquip.class));
            preventEquips.addAll(classType.getOfType(PreventEquip.class));
            for (PreventEquip preventEquip : preventEquips) {
                boolean cannotEquip = false;
                for (BurnInLightAttribute.ArmorSlot armorSlot : BurnInLightAttribute.ArmorSlot.values()) {
                    if (cannotEquip) break;
                    if (!preventEquip.canEquip(player, stack, armorSlot.slot)) {
                        cannotEquip = true;
                    }
                }
                if (cannotEquip) {
                    event.getToolTip().add(preventEquip.tooltip(stack, event.getFlags(), event.getContext()));
                }
            }
        }
    }
}
