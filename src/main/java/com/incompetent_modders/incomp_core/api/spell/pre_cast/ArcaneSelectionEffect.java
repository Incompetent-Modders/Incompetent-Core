package com.incompetent_modders.incomp_core.api.spell.pre_cast;

import com.incompetent_modders.incomp_core.api.effect.MagicallyAppliedEffect;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class ArcaneSelectionEffect extends MagicallyAppliedEffect {
    public ArcaneSelectionEffect() {
        super(MobEffectCategory.NEUTRAL, 0x71f9f0);
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        int i = entity.getEffect(this).getDuration();
        if (!entity.getCommandSenderWorld().isClientSide()) {
            Scoreboard scoreboard = entity.level().getScoreboard();
            PlayerTeam selectedTeam = CommonUtils.createTeam(scoreboard, "ArcaneSelection", ChatFormatting.GOLD);
            scoreboard.addPlayerToTeam(entity.getScoreboardName(), selectedTeam);
            MobEffectInstance mobEffectInstance = new MobEffectInstance(MobEffects.GLOWING, 5, 1, false, false);
            entity.addEffect(mobEffectInstance);
            CompoundTag tag = entity.getPersistentData();
            tag.putBoolean("Glowing", true);
            
            if (i <= 5) {
                CommonUtils.removeTeam(scoreboard, selectedTeam);
            }
        }
        
    }
}
