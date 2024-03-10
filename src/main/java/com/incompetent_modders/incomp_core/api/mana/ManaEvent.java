package com.incompetent_modders.incomp_core.api.mana;

import com.incompetent_modders.incomp_core.api.spell.SpellEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class ManaEvent extends LivingEvent implements ICancellableEvent {
    private double amount;
    
    public ManaEvent(LivingEntity entity, double amount) {
        super(entity);
        this.setAmount(amount);
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public static class ManaRegenEvent extends ManaEvent {
        public final double amount;
        public final LivingEntity entity;
        public ManaRegenEvent(LivingEntity entity, double amount) {
            super(entity, amount);
            this.amount = amount;
            this.entity = entity;
        }
        public double getAmount() {
            return amount;
        }
        
        public LivingEntity getEntity() {
            return entity;
        }
        
    }
}
