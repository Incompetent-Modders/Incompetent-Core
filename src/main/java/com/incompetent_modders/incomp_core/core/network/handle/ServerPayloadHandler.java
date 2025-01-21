package com.incompetent_modders.incomp_core.core.network.handle;

import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.core.def.ClassType;
import com.incompetent_modders.incomp_core.core.def.SpeciesType;
import com.incompetent_modders.incomp_core.core.network.serverbound.ClassAbilityPayload;
import com.incompetent_modders.incomp_core.core.network.serverbound.ScrollSpellSlotPacket;
import com.incompetent_modders.incomp_core.core.network.serverbound.SpeciesAbilityPayload;
import com.incompetent_modders.incomp_core.core.player.class_type.ClassTypeProvider;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.incompetent_modders.incomp_core.core.player.species_type.SpeciesTypeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    private final Minecraft minecraft = Minecraft.getInstance();

    private ServerPayloadHandler() {
    }

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handle(ClassAbilityPayload payload, IPayloadContext context) {
        Player player = context.player();
        ClassType classType = PlayerDataHelper.getClassType(player);
        ClassTypeProvider classTypeProvider = PlayerDataHelper.getClassTypeProvider(player).orElseThrow();
        boolean canUseAbility = classTypeProvider.canUseAbility();
        if (canUseAbility) {
            classType.ability().apply(player.level(), player);
            classTypeProvider.setAbilityCooldown(payload.applyCooldown() ? classType.abilityCooldown() : 0);
        }
    }

    public void handle(SpeciesAbilityPayload payload, IPayloadContext context) {
        Player player = context.player();
        SpeciesType speciesType = PlayerDataHelper.getSpeciesType(player);
        SpeciesTypeProvider speciesTypeProvider = PlayerDataHelper.getSpeciesTypeProvider(player).orElseThrow();
        boolean canUseAbility = speciesTypeProvider.canUseAbility();
        if (canUseAbility) {
            speciesType.ability().apply(player.level(), player);
            speciesTypeProvider.setAbilityCooldown(payload.applyCooldown() ? speciesType.abilityCooldown() : 0);
        }
    }

    public void handle(ScrollSpellSlotPacket packet, IPayloadContext context) {
        Player player = context.player();
        ItemStack equipped = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (equipped.getItem() instanceof SpellCastingItem) {
            ScrollSpellSlotPacket.changeSelectedSpell(equipped, packet.forward());
        }
    }
}
