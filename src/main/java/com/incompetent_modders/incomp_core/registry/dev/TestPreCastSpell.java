package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.api.spell.PreCastSpell;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.api.spell.SpellCategory;
import com.incompetent_modders.incomp_core.api.spell.SpellUtils;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class TestPreCastSpell extends Spell implements PreCastSpell<TestRangedSpell> {
    
    public TestPreCastSpell() {
        super(0, 5, 120, SpellCategory.OFFENSE);
    }
    @Override
    public Class<TestRangedSpell> getSpellClass() {
        return TestRangedSpell.class;
    }
    
    @Override
    public int minSelections() {
        return 1;
    }
    
    @Override
    public int maxSelections() {
        return 15;
    }
    
    @Override
    public void onPreCast(Level level, LivingEntity caster, InteractionHand hand) {
        if (caster instanceof Player playerCaster) {
            HitResult result = SpellUtils.genericSpellRayTrace(playerCaster);
            if (result instanceof EntityHitResult entityHit) {
                Entity target = entityHit.getEntity();
                if (target instanceof LivingEntity livingTarget) {
                    this.selectEntity(playerCaster, level, livingTarget, true);
                }
            }
        }
    }
    
    @Override
    public void writeToCaster(Level level, Player entity, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag selectedSpell = tag.getCompound("selectedSpell");
        CompoundTag preCastTag = selectedSpell.getCompound("PreCast");
        selectedEntities.forEach(selected -> {
            CompoundTag entityTag = preCastTag.getCompound("SelectedEntities");
            entityTag.putUUID("uuid", selected.getUUID());
        });
    }
    
    @Override
    public void onCast(Level level, LivingEntity entity, InteractionHand hand) {
        super.onCast(level, entity, hand);
        selectedEntities.forEach(selected -> {
            //selected.addEffect(new MobEffectInstance(MobEffects.LEVITATION, (int) CommonUtils.secondsToTicks(10), 1));
            for (int i = 0; i < 5; i++) {
                level.addFreshEntity(new ShulkerBullet(level, entity, selected, Direction.Axis.Y));
            }
            level.playSound(entity, entity.getOnPos(), SoundEvents.SHULKER_SHOOT, SoundSource.PLAYERS, 2.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
        });
    }
}
