package com.incompetent_modders.incomp_core.api.ability;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Ability {
    private String descriptionId;
    
    public void apply(Level level, Player player) {
    
    }
    
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("ability", ModRegistries.ABILITY.getKey(this));
        }
        
        return this.descriptionId;
    }
    
    protected String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }
    
    public Component getDisplayName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }
    public Component getDescription() {
        return Component.translatable(this.getOrCreateDescriptionId() + ".info");
    }
    
    public ResourceLocation getAbilityIdentifier() {
        return ModRegistries.ABILITY.getKey(this);
    }
}
