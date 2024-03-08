package com.incompetent_modders.incomp_core.api.mana;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class MaxManaCalcEvent extends LivingEvent {
    
    private int max;
    private float reserve;
    
    public MaxManaCalcEvent(LivingEntity entity, int max) {
        super(entity);
        this.max = max;
        this.reserve = 0;
    }
    
    public void setMax(int newMax) {
        this.max = Math.max(newMax, 0);
    }
    
    public int getMax() {
        return this.max;
    }
    
    public void setReserve(float newReserve) {
        this.reserve = Mth.clamp(0, newReserve, 1);
    }
    public float getReserve() {
        return this.reserve;
    }
}
