package com.incompetent_modders.incomp_core.api.spell;

import com.incompetent_modders.incomp_core.ClientUtil;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.data.IncompBlockTagsProvider;
import com.incompetent_modders.incomp_core.registry.ModEffects;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SplashPotionItem;
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
    
    default void selectEntity(Player player, Level level, Entity target, boolean onlyLiving) {
        if (onlyLiving && target instanceof LivingEntity livingTarget) {
            if (selectedEntities.contains(livingTarget))
                return;
            selectedEntities.add(livingTarget);
            CommonUtils.onEntitySelectEvent(level, livingTarget);
            player.displayClientMessage(Component.translatable("spell.incompetent_core.select_entity").append(entityName(livingTarget)), true);
        }
    }
    default Component entityName(LivingEntity entity) {
        if (entity.hasCustomName()) {
            return Component.literal(entity.getDisplayName().getString() + " (" + entity.getType().getDescription().getString() + ")");
        }
        return entity.getType().getDescription();
    }
    default void stopPreCast(Level level) {
        selectedEntities.forEach(entity -> CommonUtils.onEntityDeselectEvent(level, entity));
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
        IncompCore.LOGGER.info(player.getDisplayName().getString() + " Has selected a block at: " + pos.toString() + " (" + blockName.getString() + ")");
    }
    
    default void afterCast(Level level) {
        selectedPositions.forEach(pos -> spawnCastParticles(level, pos));
        selectedEntities.clear();
        selectedPositions.clear();
    }
    default void spawnCastParticles(Level level, BlockPos pos) {
        ClientUtil.createCubeOutlineParticle(pos, level, ParticleTypes.GLOW);
    }
}
