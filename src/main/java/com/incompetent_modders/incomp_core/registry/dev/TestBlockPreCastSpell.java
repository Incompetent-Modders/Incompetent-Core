package com.incompetent_modders.incomp_core.registry.dev;

import com.incompetent_modders.incomp_core.api.spell.PreCastSpell;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.api.spell.SpellCategory;
import com.incompetent_modders.incomp_core.api.spell.SpellUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class TestBlockPreCastSpell extends Spell implements PreCastSpell<TestBlockPreCastSpell> {
    
    public TestBlockPreCastSpell() {
        super(SpellCategory.ENVIRONMENTAL);
    }
    @Override
    public Class<TestBlockPreCastSpell> getSpellClass() {
        return TestBlockPreCastSpell.class;
    }
    
    @Override
    public int minSelections() {
        return 1;
    }
    
    @Override
    public int maxSelections() {
        return 5;
    }
    
    @Override
    public void onPreCast(Level level, LivingEntity caster, InteractionHand hand) {
        if (caster instanceof Player playerCaster) {
            HitResult result = SpellUtils.genericSpellRayTrace(playerCaster);
            if (result instanceof BlockHitResult blockHit) {
                BlockPos targetPos = blockHit.getBlockPos();
                this.selectPosition(playerCaster, targetPos, level);
            }
        }
    }
    
    @Override
    public void onCast(Level level, LivingEntity entity, InteractionHand hand) {
        super.onCast(level, entity, hand);
        selectedPositions.forEach(selected -> {
            //selected.addEffect(new MobEffectInstance(MobEffects.LEVITATION, (int) CommonUtils.secondsToTicks(10), 1));
            level.setBlockAndUpdate(selected, net.minecraft.world.level.block.Blocks.DIAMOND_BLOCK.defaultBlockState());
        });
    }
}
