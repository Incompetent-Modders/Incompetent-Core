package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.api.entity.projectile.SpellProjectile;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.api.spell.SpellCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import static com.incompetent_modders.incomp_core.util.CommonUtils.secondsToTicks;

public class TestProjectileSpell extends Spell {
    public TestProjectileSpell() {
        super(true, 0, (int) secondsToTicks(2), (int) secondsToTicks(5), SpellCategory.PROJECTILE);
    }
    @Override
    public void onCast(Level level, Player player, InteractionHand hand) {
        super.onCast(level, player, hand);
        if (!level.isClientSide) {
            SpellProjectile spellProjectile = new SpellProjectile(player, level);
            CompoundTag tag = spellProjectile.getPersistentData();
            tag.putFloat("Gravity", 0.25F);
            tag.putFloat("ExplosivePower", 7.0F);
            tag.putInt("Color", 0x00FF00);
            spellProjectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(spellProjectile);
            player.displayClientMessage(Component.literal("Debug: Projectile spawned"), true);
        }
    }
    @Override
    public void onCast(Level level, LivingEntity entity, InteractionHand hand) {
        super.onCast(level, entity, hand);
        if (!level.isClientSide) {
            SpellProjectile spellProjectile = new SpellProjectile(entity, level);
            CompoundTag tag = spellProjectile.getPersistentData();
            tag.putFloat("Gravity", 0.25F);
            tag.putFloat("ExplosivePower", 7.0F);
            tag.putInt("Color", 0x00FF00);
            spellProjectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(spellProjectile);
        }
    }
    
    public void onHit(Level level, SpellProjectile entity, HitResult hitresult) {
        boolean flag = false;
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult) hitresult).getBlockPos();
            level.explode(entity, blockpos.getX(), blockpos.getY(), blockpos.getZ(), 7.0F, false, Level.ExplosionInteraction.BLOCK);
        }
        if (hitresult.getType() != HitResult.Type.MISS && !flag && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(entity, hitresult)) {
            entity.onHit(hitresult);
        }
        
    }
}
