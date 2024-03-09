package com.incompetent_modders.incomp_core.api.class_type;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class ClassType {
    private final boolean canCastSpells;
    private final int baseMana;
    private final boolean pacifist;
    @Nullable
    private String descriptionId;
    
    public ClassType(boolean canCastSpells, int baseMana, boolean pacifist) {
        this.canCastSpells = canCastSpells;
        this.baseMana = baseMana;
        this.pacifist = pacifist;
    }
    
    public ClassType() {
        this.canCastSpells = false;
        this.baseMana = 0;
        this.pacifist = false;
    }
    
    public int getBaseMana() {
        return baseMana;
    }
    public boolean canCastSpells() {
        return canCastSpells;
    }
    public boolean isPacifist() {
        return pacifist;
    }
    public final ClassType getClassType(ResourceLocation rl) {
        if (rl.equals(this.getClassTypeIdentifier())) {
            return this;
        } else {
            IncompCore.LOGGER.error("Class Type: " + rl + " does not exist!");
            return null;
        }
    }
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("class_type", ModRegistries.CLASS_TYPE.getKey(this));
        }
        
        return this.descriptionId;
    }
    public Component getDisplayName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }
    public ResourceLocation getClassTypeIdentifier() {
        return ModRegistries.CLASS_TYPE.getKey(this);
    }
    
    public static final ClassType NONE = new ClassType();
    
    public static void classPassiveEffect() {
    
    }
    
    public static void classAbility() {
    
    }
}
