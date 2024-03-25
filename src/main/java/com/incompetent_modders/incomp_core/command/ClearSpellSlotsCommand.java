package com.incompetent_modders.incomp_core.command;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.api.spell.SpellUtils;
import com.incompetent_modders.incomp_core.api.spell.Spells;
import com.mojang.brigadier.arguments.BoolArgumentType;
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

public class ClearSpellSlotsCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("clearSpellSlot").requires(s -> s.hasPermission(2)).then(Commands.argument("spellSlot", IntegerArgumentType.integer(0, 5)).executes(arguments -> {
            ServerLevel world = arguments.getSource().getLevel();
            Player player = (Player) arguments.getSource().getEntity();
            if (player == null)
                player = FakePlayerFactory.getMinecraft(world);
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpellCastingItem) {
                ItemStack staff = player.getItemInHand(InteractionHand.MAIN_HAND);
                CompoundTag tag = staff.getOrCreateTag();
                Spell spell = Spells.EMPTY.get();
                SpellUtils.serializeToSlot(tag, IntegerArgumentType.getInteger(arguments, "spellSlot"), spell);
                Component spellComponent = Component.translatable("commands.clear_spell.single", IntegerArgumentType.getInteger(arguments, "spellSlot"));
                player.displayClientMessage(spellComponent, false);
            }
            return 0;
        })).then(Commands.argument("clearAllSlots", BoolArgumentType.bool()).executes(arguments -> {
            ServerLevel world = arguments.getSource().getLevel();
            Player player = (Player) arguments.getSource().getEntity();
            if (player == null)
                player = FakePlayerFactory.getMinecraft(world);
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpellCastingItem spellCastingItem) {
                ItemStack staff = player.getItemInHand(InteractionHand.MAIN_HAND);
                CompoundTag tag = staff.getOrCreateTag();
                int slotsAmount = SpellCastingItem.getSpellSlots(spellCastingItem.getLevel()) - 1;
                for (int i = 0; i <= slotsAmount; i++) {
                    Spell spell = Spells.EMPTY.get();
                    SpellUtils.serializeToSlot(tag, i, spell);
                }
                Component spellComponent = Component.translatable("commands.clear_spell.all");
                player.displayClientMessage(spellComponent, false);
            }
            return 0;
        }));
    }
}