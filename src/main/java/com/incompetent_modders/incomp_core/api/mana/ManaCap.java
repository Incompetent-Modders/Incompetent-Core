package com.incompetent_modders.incomp_core.api.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class ManaCap implements IManaCap {
    private final LivingEntity livingEntity;
    
    private double mana;
    
    private int maxMana;
    
    public ManaCap(@Nullable final LivingEntity entity) {
        this.livingEntity = entity;
    }
    
    @Override
    public double getCurrentMana() {
        return mana;
    }
    
    @Override
    public int getMaxMana() {
        return maxMana;
    }
    
    @Override
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }
    
    @Override
    public double setMana(double mana) {
        if (mana > getMaxMana()) {
            this.mana = getMaxMana();
        } else if (mana < 0) {
            this.mana = 0;
        } else {
            this.mana = mana;
        }
        return this.getCurrentMana();
    }
    
    @Override
    public double addMana(double manaToAdd) {
        this.setMana(this.getCurrentMana() + manaToAdd);
        return this.getCurrentMana();
    }
    
    @Override
    public double removeMana(double manaToRemove) {
        if (manaToRemove < 0)
            manaToRemove = 0;
        this.setMana(this.getCurrentMana() - manaToRemove);
        return this.getCurrentMana();
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("currentMana", getCurrentMana());
        tag.putInt("maxMana", getMaxMana());
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag tag) {
        setMaxMana(tag.getInt("maxMana"));
        setMana(tag.getDouble("currentMana"));
    }
}
