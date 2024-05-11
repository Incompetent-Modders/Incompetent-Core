package com.incompetent_modders.incomp_core.api.spell;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.json.spell.SpellCategory;
import com.incompetent_modders.incomp_core.api.json.spell.SpellProperties;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.json.spell.SpellPropertyListener;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import com.incompetent_modders.incomp_core.registry.ModSpells;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A spell.
 * <p>
 * ModSpells are registered using the SPELL DeferredRegister in ModRegistries.
 * @see ModRegistries#SPELL
 * @see ModSpells
 */
public class Spell {
    private final Holder.Reference<Spell> builtInRegistryHolder = ModRegistries.SPELL.createIntrusiveHolder(this);
    @Nullable
    private String descriptionId;
    
    public Spell() {
    }
    
    /**
     * Returns the spells with the given ResourceLocation.
     * <p>
     * If the ResourceLocation does not exist, an error is logged and null is returned.
     * @param rl The ResourceLocation of the spells.
     * @return The spells with the given ResourceLocation.
     */
    public final Spell getSpell(ResourceLocation rl) {
        if (rl.equals(this.getSpellIdentifier())) {
            return this;
        } else {
            IncompCore.LOGGER.error("Spell " + rl + " does not exist!");
            return null;
        }
    }
    
    /**
     * Returns the category of the spells.
     * @see SpellCategory
     */
    public SpellCategory getCategory() {
        return getSpellProperties().category();
    }
    
    public SpellProperties getSpellProperties() {
        return SpellPropertyListener.getSpellProperties(this);
    }
    /**
     * Returns the spell catalyst of the spells.
     * <p>
     * The spell catalyst is the item required to cast the spells.
     */
    public ItemStack getSpellCatalyst() {
        return getSpellProperties().catalyst();
    }
    
    /**
     * Returns true if the spells has a spell catalyst.
     */
    public boolean hasSpellCatalyst() {
        return !this.getSpellCatalyst().isEmpty();
    }
    /**
     * Returns the sound of the spells.
     */
    public SoundEvent getSpellSound() {
        return getSpellProperties().castSound();
    }
    
    
    /**
     * Returns the class type of the caster of the spells.
     * <p>
     * If the spells has no class type, null is returned.
     * @see ClassType
     */
    public Collection<ClassType> getCasterClassType() {
        return getSpellProperties().classType().getClassType();
    }
    /**
     * Returns the species of the caster of the spells.
     * <p>
     * If the spells has no species, null is returned.
     * @see SpeciesType
     */
    public Collection<SpeciesType> getCasterSpeciesType() {
        return getSpellProperties().speciesType().getSpecies();
    }
    /**
     * Returns the ResourceLocation of the icon of the spells.
     * <p>
     * The icon of the spells is located in the "textures/spells" directory.
     */
    public ResourceLocation getSpellIconLocation() {
        return new ResourceLocation(this.getSpellIdentifier().getNamespace(), "spells/" + this.getSpellIdentifier().getPath());
    }
    
    /**
     * Returns the draw time of the spells.
     * <p>
     * The draw time is the time it takes to cast the spells.
     */
    public int getDrawTime() {
        return getSpellProperties().drawTime();
    }
    
    
    /**
     * Returns the description id of the spells.
     * <p>
     * The description id is used to display the name of the spells in the game.
     */
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("spell", ModRegistries.SPELL.getKey(this));
        }
        
        return this.descriptionId;
    }
    
    /**
     * Returns the display name of the spells.
     */
    public Component getDisplayName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }
    
    /**
     * Returns the ResourceLocation of the spells.
     */
    public ResourceLocation getSpellIdentifier() {
        return ModRegistries.SPELL.getKey(this);
    }
    
    
    public Holder.Reference<Spell> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }
    
    public static final Codec<Holder<Spell>> DIRECT_CODEC = ModRegistries.SPELL
            .holderByNameCodec()
            .validate(
                    DataResult::success
            );
}
