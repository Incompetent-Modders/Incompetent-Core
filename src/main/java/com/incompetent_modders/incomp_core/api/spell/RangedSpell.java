package com.incompetent_modders.incomp_core.api.spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class RangedSpell extends Spell {
    public RangedSpell(int manaCost, int drawTime, int coolDown, ResourceLocation casterClassType) {
        super(manaCost, drawTime, coolDown, SpellCategory.RANGED, casterClassType);
    }
    public RangedSpell(int manaCost, int drawTime, int coolDown) {
        super(manaCost, drawTime, coolDown, SpellCategory.RANGED);
    }
    /**
     * Called when the spell hits an object.
     *
     * @param level     The level in which the spell hits the entity.
     * @param hitresult The result of the hit.
     */
    //ONLY USE IF YOU ARE CREATING A SPELL PROJECTILE ENTITY
    //IF YOU ARE USING AN EXISTING PROJECTILE, DO NOT USE THIS METHOD
    public void onHit(Level level, LivingEntity entity, HitResult hitresult) {
    }
    
    @Override
    protected void onCast(Level level, LivingEntity entity, InteractionHand hand) {
        super.onCast(level, entity, hand);
        if (entity instanceof Player playerCaster) {
            HitResult result = SpellUtils.genericSpellRayTrace(playerCaster);
            onHit(level, entity, result);
        }
    }
}
