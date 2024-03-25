package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.api.spell.RangedSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import static com.incompetent_modders.incomp_core.util.CommonUtils.secondsToTicks;

public class TestRangedSpell extends RangedSpell {
    public TestRangedSpell() {
        super(0, (int) secondsToTicks(2), (int) secondsToTicks(5));
    }
    @Override
    public void onCast(Level level, LivingEntity entity, InteractionHand hand) {
        super.onCast(level, entity, hand);
        if (entity instanceof Player player) {
            player.displayClientMessage(Component.literal("Debug: Projectile spawned"), true);
        }
    }
    public void onHit(Level level, LivingEntity entity, HitResult hitresult) {
        if (hitresult instanceof BlockHitResult blockHit) {
            BlockPos blockpos = blockHit.getBlockPos();
            level.explode(entity, blockpos.getX(), blockpos.getY(), blockpos.getZ(), 13.0F, false, Level.ExplosionInteraction.BLOCK);
        }
    }
}
