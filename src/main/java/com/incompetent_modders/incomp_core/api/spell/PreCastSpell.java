package com.incompetent_modders.incomp_core.api.spell;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.data.IncompBlockTagsProvider;
import com.incompetent_modders.incomp_core.registry.ModEffects;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface PreCastSpell<T extends Spell> {
    List<LivingEntity> selectedEntities = new ArrayList<>();
    List<BlockPos> selectedPositions = new ArrayList<>();
    Class<T> getSpellClass();
    int minSelections();
    int maxSelections();
    default boolean onlySurface() {
        return false;
    }
    default boolean canStopPreCast() {
        return selectedEntities.size() >= minSelections() || selectedPositions.size() >= minSelections();
    }
    default List<LivingEntity> getSelectedEntities() {
        return selectedEntities;
    }
    default List<BlockPos> getSelectedPositions() {
        return selectedPositions;
    }
    void onPreCast(Level level, LivingEntity entity, InteractionHand hand);
    
    void writeToCaster(Level level, Player entity, ItemStack stack);
    
    default void selectEntity(Player player, Level level, Entity target, boolean onlyLiving) {
        if (onlyLiving && target instanceof LivingEntity livingTarget) {
            if (selectedEntities.contains(livingTarget))
                return;
            livingTarget.addEffect(new MobEffectInstance(MobEffects.GLOWING, 5, 0, true, true, false), livingTarget);
            livingTarget.addEffect(new MobEffectInstance(ModEffects.ARCANE_SELECTION.get(), 999999, 1, true, true, false), livingTarget);
            selectedEntities.add(livingTarget);
            player.displayClientMessage(Component.translatable("spell.incompetent_core.select_entity").append(entityName(livingTarget)), true);
            IncompCore.LOGGER.info(player.getDisplayName().getString() + " Has selected a: " + target.getDisplayName().getString());
        }
    }
    default Component entityName(LivingEntity entity) {
        if (entity.hasCustomName()) {
            return Component.literal(entity.getDisplayName().getString() + " (" + entity.getType().getDescription().getString() + ")");
        }
        return entity.getType().getDescription();
    }
    default void stopPreCast() {
        selectedEntities.forEach(selected -> {
            Scoreboard scoreboard = selected.level().getScoreboard();
            PlayerTeam selectedTeam = CommonUtils.createTeam(scoreboard, "ArcaneSelection", ChatFormatting.GOLD);
            selected.removeEffect(ModEffects.ARCANE_SELECTION.get());
            CommonUtils.removeTeam(scoreboard, selectedTeam);
        });
    }
    default void selectPosition(Player player, BlockPos pos, Level level) {
        if (selectedPositions.contains(pos))
            return;
        if (onlySurface()) {
            if (!level.getBlockState(pos.above()).is(IncompBlockTagsProvider.modBlockTag("spell_transparent"))) {
                selectedPositions.remove(pos);
                player.displayClientMessage(Component.translatable("spell.incompetent_core.select_position.invalid.block_above"), true);
                return;
            }
        }
        selectedPositions.add(pos);
        Component blockName = Component.translatable(level.getBlockState(pos).getBlock().getDescriptionId());
        player.displayClientMessage(Component.translatable("spell.incompetent_core.select_position", blockName, pos.toString()), true);
        IncompCore.LOGGER.info(player.getDisplayName().getString() + " Has selected a block at: " + pos + " (" + blockName.getString() + ")");
    }
    
    default void afterCast() {
        selectedEntities.clear();
        selectedPositions.clear();
    }
}
