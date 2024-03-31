package com.incompetent_modders.incomp_core.api.class_type;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.network.packets.IncompPlayerDataSyncPacket;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

import javax.annotation.Nullable;

/**
 * The Class Type of a player.
 * <p>
 * The class type determines the abilities and stats of a player.
 * <p>
 * Class types are registered using the CLASS_TYPE DeferredRegister in ModRegistries.
 * @see ModRegistries#CLASS_TYPE
 * @see ModClassTypes
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClassType {
    private final boolean canCastSpells;
    private final int maxMana;
    private final boolean pacifist;
    private final boolean useClassSpecificTexture;
    @Nullable
    private String descriptionId;
    private final int passiveEffectTickFrequency;
    
    public ClassType(boolean canCastSpells, int maxMana, boolean pacifist, int passiveEffectTickFrequency) {
        this.canCastSpells = canCastSpells;
        this.maxMana = maxMana;
        this.pacifist = pacifist;
        this.passiveEffectTickFrequency = passiveEffectTickFrequency;
        this.useClassSpecificTexture = true;
    }
    public ClassType(boolean canCastSpells, int maxMana, boolean pacifist, int passiveEffectTickFrequency, boolean useClassSpecificTexture) {
        this.canCastSpells = canCastSpells;
        this.maxMana = maxMana;
        this.pacifist = pacifist;
        this.passiveEffectTickFrequency = passiveEffectTickFrequency;
        this.useClassSpecificTexture = useClassSpecificTexture;
    }
    /**
     * Creates a class type with no stats.
     * <p>
     * Used for the SIMPLE_HUMAN class type.
     * @see ModClassTypes#SIMPLE_HUMAN
     */
    public ClassType() {
        this.canCastSpells = false;
        this.maxMana = 0;
        this.pacifist = false;
        this.passiveEffectTickFrequency = 0;
        this.useClassSpecificTexture = false;
    }
    
    /**
     * Returns the maximum mana of the class type.
     * <p>
     * If the class type cannot cast spells, the maximum mana is 0.
     */
    public int getMaxMana() {
        return canCastSpells ? maxMana : 0;
    }
    
    /**
     * Returns true if the class type can cast spells.
     * <p>
     * A class type that can cast spells has a mana bar and can cast spells.
     */
    public boolean canCastSpells() {
        return canCastSpells;
    }
    
    /**
     * Returns true if the class type is pacifist.
     * <p>
     * A pacifist class type has decreased damage.
     */
    public boolean isPacifist() {
        return pacifist;
    }
    
    /**
     * Returns the frequency at which the passive effect of the class type is applied.
     */
    public int getPassiveEffectTickFrequency() {
        return passiveEffectTickFrequency;
    }
    
    /**
     * Returns the class type with the specified identifier.
     * <p>
     * If no class type with the specified identifier exists, an error message is logged and null is returned.
     * @param rl The identifier of the class type.
     * @return The class type with the specified identifier.
     */
    public final ClassType getClassType(ResourceLocation rl) {
        if (rl.equals(this.getClassTypeIdentifier())) {
            return this;
        } else {
            IncompCore.LOGGER.error("Class Type: " + rl + " does not exist!");
            return null;
        }
    }
    
    /**
     * Returns the description identifier of the class type.
     * <p>
     * Used in the translation file to display the name of the class type.
     */
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("class_type", ModRegistries.CLASS_TYPE.getKey(this));
        }
        
        return this.descriptionId;
    }
    
    /**
     * Returns the display name of the class type.
     */
    public Component getDisplayName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }
    
    /**
     * Returns the identifier of the class type.
     */
    public ResourceLocation getClassTypeIdentifier() {
        return ModRegistries.CLASS_TYPE.getKey(this);
    }
    
    
    /**
     * The passive effect of the class type.
     * <p>
     * This method is called every tick but only applies the effect at the frequency specified by the passiveEffectTickFrequency field.
     * <p>
     * If left empty, the class type will have no passive effect.
     */
    public void classPassiveEffect() {
    
    }
    
    /**
     * The ability of the class type.
     * <p>
     * This method is called when the player presses the ability key.
     * <p>
     * If left empty, the class type will have no ability.
     */
    public static void classAbility() {
    
    }
    
    /**
     * Returns true if the class type can regenerate mana.
     * <p>
     * If left empty, the class type will be able to regenerate mana.
     * <p>
     * <b>Useful for classes that need to meet certain conditions to regenerate mana.</b>
     */
    public boolean canRegenerateMana() {
        return true;
    }
    
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide) {
            return;
        }
        if (PlayerDataCore.ClassData.getPlayerClassType(event.player) == null) {
            PlayerDataCore.ClassData.setPlayerClassType(event.player, ModClassTypes.SIMPLE_HUMAN.get());
            return;
        }
        if (PlayerDataCore.ClassData.getPlayerClassType(event.player).getPassiveEffectTickFrequency() == 0) {
            return;
        }
        // Only call the method if the tickFrequency is less than or equal to 0 and then reset the tickFrequency to the passiveEffectTickFrequency
        if (event.player.tickCount % PlayerDataCore.ClassData.getPlayerClassType(event.player).getPassiveEffectTickFrequency() == 0) {
            PlayerDataCore.ClassData.getPlayerClassType(event.player).classPassiveEffect();
        }
    }
    
    public boolean useClassSpecificTexture() {
        return useClassSpecificTexture;
    }
    public ResourceLocation getClassSpecificTexture(String path, String name) {
        return new ResourceLocation(this.getClassTypeIdentifier().getNamespace(), "textures/" + path + "/" + this.getClassTypeIdentifier().getPath() + "/" + name);
    }
    public ResourceLocation getSpellOverlayTexture(String spriteName) {
        if (!useClassSpecificTexture)
            return new ResourceLocation(IncompCore.MODID, "spell_list/" + spriteName);
        return new ResourceLocation(this.getClassTypeIdentifier().getNamespace(), "spell_list/" + this.getClassTypeIdentifier().getPath() + "/" + spriteName);
    }
}
