package com.incompetent_modders.incomp_core.command;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.spell.EmptySpell;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.api.spell.Spells;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public class WhatSpellIsInSlotCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("getSpell").requires(s -> s.hasPermission(2)).then(Commands.argument("spellSlot", IntegerArgumentType.integer(0, 5)).executes(arguments -> {
            ServerLevel world = arguments.getSource().getLevel();
            Player player = (Player) arguments.getSource().getEntity();
            if (player == null)
                player = FakePlayerFactory.getMinecraft(world);
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpellCastingItem) {
                ItemStack staff = player.getItemInHand(InteractionHand.MAIN_HAND);
                CompoundTag tag = staff.getOrCreateTag();
                String spellString = tag.getString("spellSlot_" + IntegerArgumentType.getInteger(arguments, "spellSlot"));
                Spell spell = ModRegistries.SPELL.get(new ResourceLocation(spellString));
                Component spellComponent = isSpellPresent(spell) ? spell.getDisplayName() : Component.nullToEmpty("No spell in slot " + IntegerArgumentType.getInteger(arguments, "spellSlot"));
                player.displayClientMessage(spellComponent, false);
            }
            return 0;
        }));
    }
    
    private static boolean isSpellPresent(Spell spell) {
        if (spell instanceof EmptySpell) {
            return false;
        }
        return spell != null;
    }
}
