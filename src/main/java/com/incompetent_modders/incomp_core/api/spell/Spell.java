package com.incompetent_modders.incomp_core.api.spell;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.registry.ModCapabilities;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * A spell.
 * <p>
 * Spells are registered using the SPELL DeferredRegister in ModRegistries.
 * @see ModRegistries#SPELL
 * @see Spells
 */
public class Spell {
    private final boolean isRangedAttack;
    private final int manaCost;
    private final int drawTime;
    private final int coolDown;
    private final SpellCategory category;
    private final ClassType casterClassType;
    @Nullable
    private String descriptionId;
    
    public Spell(boolean isRangedAttack, int manaCost, int drawTime, int coolDown, SpellCategory category, ResourceLocation casterClassType) {
        this.isRangedAttack = isRangedAttack;
        this.manaCost = manaCost;
        this.drawTime = drawTime;
        this.coolDown = coolDown;
        this.category = category;
        this.casterClassType = ModRegistries.CLASS_TYPE.get(casterClassType);
    }
    public Spell(boolean isRangedAttack, int manaCost, int drawTime, int coolDown, SpellCategory category) {
        this.isRangedAttack = isRangedAttack;
        this.manaCost = manaCost;
        this.drawTime = drawTime;
        this.coolDown = coolDown;
        this.category = category;
        this.casterClassType = null;
    }
    
    /**
     * Creates a spell with no stats.
     * <p>
     * Used for the EMPTY spell.
     * @see Spells#EMPTY
     */
    public Spell() {
        this.isRangedAttack = false;
        this.manaCost = 0;
        this.drawTime = 0;
        this.coolDown = 0;
        this.category = SpellCategory.DEBUFF;
        this.casterClassType = ModRegistries.CLASS_TYPE.get(ModClassTypes.SIMPLE_HUMAN.get().getClassTypeIdentifier());
    }
    
    /**
     * Returns the spell with the given ResourceLocation.
     * <p>
     * If the ResourceLocation does not exist, an error is logged and null is returned.
     * @param rl The ResourceLocation of the spell.
     * @return The spell with the given ResourceLocation.
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
     * Returns the category of the spell.
     * @see SpellCategory
     */
    public SpellCategory getCategory() {
        return category;
    }
    
    /**
     * Returns the sound of the spell.
     * <p>
     * The sound of the spell is determined by the category of the spell.
     * @see SpellCategory
     * @see Spell#getSpellSound(SpellCategory)
     */
    public SoundEvent getSpellSound() {
        return getSpellSound(this.getCategory());
    }
    private SoundEvent getSpellSound(SpellCategory category) {
        return switch (category) {
            case CURSE -> SoundEvents.ALLAY_DEATH;
            case PROJECTILE -> SoundEvents.ALLAY_ITEM_TAKEN;
            case BUFF -> SoundEvents.WARDEN_AGITATED;
            case DEBUFF -> SoundEvents.WARDEN_HURT;
            case HEALING -> SoundEvents.ALLAY_AMBIENT_WITH_ITEM;
            case SUMMON -> SoundEvents.ALLAY_ITEM_GIVEN;
            case UTILITY -> SoundEvents.WARDEN_AMBIENT;
            case DEFENSE -> SoundEvents.WARDEN_DEATH;
            case OFFENSE -> SoundEvents.WARDEN_ATTACK_IMPACT;
            case MOBILITY -> SoundEvents.ALLAY_THROW;
            case ENVIRONMENTAL -> SoundEvents.WARDEN_EMERGE;
            default -> SoundEvents.WARDEN_HEARTBEAT;
        };
    }
    
    /**
     * Returns the class type of the caster of the spell.
     * <p>
     * If the spell has no class type, null is returned.
     * @see ClassType
     */
    public ClassType getCasterClassType() {
        return casterClassType;
    }
    
    /**
     * Returns the ResourceLocation of the icon of the spell.
     * <p>
     * The icon of the spell is located in the "textures/spells" directory.
     */
    public ResourceLocation getSpellIconLocation() {
        return new ResourceLocation(this.getSpellIdentifier().getNamespace(), "textures/spells/" + this.getSpellIdentifier().getPath());
    }
    
    /**
     * Returns the draw time of the spell.
     * <p>
     * The draw time is the time it takes to cast the spell.
     */
    public int getDrawTime() {
        return drawTime;
    }
    
    /**
     * Returns the cooldown of the spell.
     * <p>
     * The cooldown is the time it takes to cast the spell again.
     */
    public int getCoolDown() {
        return coolDown;
    }
    
    
    /**
     * Returns the description id of the spell.
     * <p>
     * The description id is used to display the name of the spell in the game.
     */
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("spell", ModRegistries.SPELL.getKey(this));
        }
        
        return this.descriptionId;
    }
    
    /**
     * Returns the display name of the spell.
     */
    public Component getDisplayName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }
    
    /**
     * Returns the ResourceLocation of the spell.
     */
    public ResourceLocation getSpellIdentifier() {
        return ModRegistries.SPELL.getKey(this);
    }
    
    /**
     * Returns the mana cost of the spell.
     * <p>
     * The mana cost is the amount of mana required to cast the spell.
     */
    public int getManaCost() {
        return manaCost;
    }
    
    /**
     * Returns true if the spell is a ranged attack.
     * <p>
     * A ranged attack is a spell that can be cast from a distance.
     */
    public boolean isRangedAttack() {
        return isRangedAttack;
    }
    
    /**
     * Casts the spell.
     * <p>
     * If the spell is cast by a player, the player's mana is reduced by the mana cost of the spell.
     * <p>
     * The spell is cast by a player, Triggers the CastEvent.
     * @param level The level in which the spell is cast.
     * @param player The player that casts the spell.
     * @param hand The hand in which the spell is cast.
     * @see CommonUtils#onCastEvent(Level, Player, InteractionHand)
     * @see SpellEvent.CastEvent
     */
    protected void onCast(Level level, Player player, InteractionHand hand) {
        CommonUtils.onCastEvent(level, player, hand);
        SpellUtils.removeMana(player, this.getManaCost());
    }
    
    /**
     * Casts the spell.
     * <p>
     * The spell is cast by an entity, Triggers the EntityCastEvent.
     * @param level The level in which the spell is cast.
     * @param entity The entity that casts the spell.
     * @param hand The hand in which the spell is cast.
     * @see CommonUtils#onEntityCastEvent(Level, Entity, InteractionHand)
     * @see SpellEvent.EntityCastEvent
     */
    protected void onCast(Level level, Entity entity, InteractionHand hand) {
        CommonUtils.onEntityCastEvent(level, entity, hand);
    }
    
    /**
     * Called when the spell is cast and fails.
     * <p>
     * If the spell is cast by a player, the player's mana is reduced by half of the mana cost of the spell.
     * @param level The level in which the spell is cast.
     * @param player The player that casts the spell.
     * @param hand The hand in which the spell is cast.
     */
    protected void onFail(Level level, Player player, InteractionHand hand) {
        SpellUtils.removeMana(player, this.getManaCost() / 2);
    }
    
    /**
     * Called when the spell is cast and fails.
     * @param level The level in which the spell is cast.
     * @param entity The entity that casts the spell.
     * @param hand The hand in which the spell is cast.
     */
    protected void onFail(Level level, Entity entity, InteractionHand hand) {
    
    }
    
    /**
     * Casts the spell.
     * <p>
     * Triggers the onCast and onFail methods according to the requirements of the spell.
     * @param level The level in which the spell is cast.
     * @param entity The entity that casts the spell.
     * @param hand The hand in which the spell is cast.
     * @param ignoreRequirements If true, the spell is cast without checking if the player can cast the spell.
     * @see Spell#onCast(Level, Entity, InteractionHand)
     * @see Spell#onCast(Level, Player, InteractionHand)
     * @see Spell#onFail(Level, Entity, InteractionHand)
     * @see Spell#onFail(Level, Player, InteractionHand)
     * @see Spell#canCast(Level, Player, InteractionHand)
     * @see Spell#shouldFail(Level, Player, InteractionHand)
     */
    public void cast(Level level, Entity entity, InteractionHand hand, boolean ignoreRequirements) {
        if (ignoreRequirements) {
            this.onCast(level, entity, hand);
        } else {
            if (entity instanceof Player player) {
                if (!this.canCast(level, player, hand)) {
                    return;
                }
                if (this.shouldFail(level, player, hand)) {
                    this.onFail(level, player, hand);
                } else {
                    this.onCast(level, player, hand);
                }
            } else {
                if (this.shouldFail(level, entity, hand)) {
                    this.onFail(level, entity, hand);
                } else {
                    this.onCast(level, entity, hand);
                }
            }
        }
    }
    
    /**
     * Should the spell fail?
     */
    protected boolean shouldFail(Level level, Player player, InteractionHand hand) {
        return false;
    }
    /**
     * Should the spell fail?
     */
    protected boolean shouldFail(Level level, Entity entity, InteractionHand hand) {
        return false;
    }
    
    /**
     * Returns true if the player can cast the spell.
     * <p>
     * A player can cast a spell if the player's class type can cast spells and the player has enough mana to cast the spell.
     * @param level The level in which the spell is cast.
     * @param player The player that casts the spell.
     * @param hand The hand in which the spell is cast.
     * @see ClassType#canCastSpells()
     * @see ModCapabilities#getMana(LivingEntity)
     */
    protected boolean canCast(Level level, Player player, InteractionHand hand) {
        if (!PlayerDataCore.getPlayerClassType(player).canCastSpells())
            return false;
        if (casterClassType != null && casterClassType.equals(PlayerDataCore.getPlayerClassType(player)))
            return true;
        if (casterClassType == null)
            return true;
        if (ModCapabilities.getMana(player).isPresent()) {
            return ModCapabilities.getMana(player).orElseThrow(NullPointerException::new).getCurrentMana() >= this.getManaCost();
        } else {
            return false;
        }
    }
    
    /**
     * Called when the spell hits an entity.
     * @param level The level in which the spell hits the entity.
     * @param entity The entity that casts the spell.
     */
    protected void onHit(Level level, Entity entity) {
    }
    
    
    /**
     * Does nothing.
     * @param level
     * @param player
     */
    protected void doNothing(Level level, Player player) {
        player.displayClientMessage(Component.translatable("incompetent_core.do_nothing"), true);
    }
}
