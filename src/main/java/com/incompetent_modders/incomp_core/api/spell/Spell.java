package com.incompetent_modders.incomp_core.api.spell;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.registry.ModCapabilities;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
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
    private final int manaCost;
    private final int drawTime;
    private final int coolDown;
    private final SpellCategory category;
    private final ClassType casterClassType;
    @Nullable
    private String descriptionId;
    
    public Spell(int manaCost, int drawTime, int coolDown, SpellCategory category, ResourceLocation casterClassType) {
        this.manaCost = manaCost;
        this.drawTime = drawTime;
        this.coolDown = coolDown;
        this.category = category;
        this.casterClassType = ModRegistries.CLASS_TYPE.get(casterClassType);
    }
    public Spell(int manaCost, int drawTime, int coolDown, SpellCategory category) {
        this.manaCost = manaCost;
        this.drawTime = drawTime;
        this.coolDown = coolDown;
        this.category = category;
        this.casterClassType = null;
    }
    
    /**
     * Creates a spells with no stats.
     * <p>
     * Used for the EMPTY spells.
     * @see Spells#EMPTY
     */
    public Spell() {
        this.manaCost = 0;
        this.drawTime = 0;
        this.coolDown = 0;
        this.category = SpellCategory.DEBUFF;
        this.casterClassType = null;
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
        return category;
    }
    
    /**
     * Returns the sound of the spells.
     * <p>
     * The sound of the spells is determined by the category of the spells.
     * @see SpellCategory
     * @see Spell#getSpellSound(SpellCategory)
     */
    public SoundEvent getSpellSound() {
        return getSpellSound(this.getCategory());
    }
    private SoundEvent getSpellSound(SpellCategory category) {
        return switch (category) {
            case CURSE -> SoundEvents.ALLAY_DEATH;
            case RANGED -> SoundEvents.ALLAY_ITEM_TAKEN;
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
     * Returns the class type of the caster of the spells.
     * <p>
     * If the spells has no class type, null is returned.
     * @see ClassType
     */
    public ClassType getCasterClassType() {
        return casterClassType;
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
        return drawTime;
    }
    
    /**
     * Returns the cooldown of the spells.
     * <p>
     * The cooldown is the time it takes to cast the spells again.
     */
    public int getCoolDown() {
        return coolDown;
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
    
    /**
     * Returns the mana cost of the spells.
     * <p>
     * The mana cost is the amount of mana required to cast the spells.
     */
    public int getManaCost() {
        return manaCost;
    }
    
    
    /**
     * Casts the spells.
     * <p>
     * The spells is cast by an entity, Triggers the EntityCastEvent.
     * @param level The level in which the spells is cast.
     * @param entity The entity that casts the spells.
     * @param hand The hand in which the spells is cast.
     * @see CommonUtils#onEntityCastEvent(Level, LivingEntity, InteractionHand)
     * @see SpellEvent.EntityCastEvent
     */
    protected void onCast(Level level, LivingEntity entity, InteractionHand hand) {
    }
    
    /**
     * Called when the spells is cast and fails.
     * @param level The level in which the spells is cast.
     * @param entity The entity that casts the spells.
     * @param hand The hand in which the spells is cast.
     */
    protected void onFail(Level level, LivingEntity entity, InteractionHand hand) {
    
    }
    
    /**
     * Casts the spells.
     * <p>
     * Triggers the onCast and onFail methods according to the requirements of the spells.
     * @param level The level in which the spells is cast.
     * @param entity The entity that casts the spells.
     * @param hand The hand in which the spells is cast.
     * @param ignoreRequirements If true, the spells is cast without checking if the player can cast the spells.
     * @see Spell#onCast(Level, LivingEntity, InteractionHand)
     * @see Spell#onFail(Level, LivingEntity, InteractionHand)
     * @see Spell#canCast(Level, Player, InteractionHand)
     * @see Spell#shouldFail(Level, Player, InteractionHand)
     */
    public void cast(Level level, LivingEntity entity, InteractionHand hand, boolean ignoreRequirements) {
        level.playSound((Player) entity, entity.getX(), entity.getY(), entity.getZ(), getSpellSound(), entity.getSoundSource(), 1.0F, 1.0F);
        if (ignoreRequirements) {
            this.onCast(level, entity, hand);
        } else {
            Player player = (Player) entity;
            if (!this.canCast(level, player, hand)) {
                this.onFail(level, player, hand);
                SpellUtils.removeMana(player, this.getManaCost() / 2);
            }
            if (this.shouldFail(level, player, hand)) {
                this.onFail(level, player, hand);
                SpellUtils.removeMana(player, this.getManaCost() / 2);
            } else {
                CommonUtils.onCastEvent(level, player, hand);
                player.awardStat(Stats.ITEM_USED.get(player.getItemInHand(hand).getItem()));
                SpellUtils.removeMana(player, this.getManaCost());
                this.onCast(level, player, hand);
            }
        }
    }
    
    /**
     * Should the spells fail?
     */
    protected boolean shouldFail(Level level, Player player, InteractionHand hand) {
        return false;
    }
    /**
     * Should the spells fail?
     */
    protected boolean shouldFail(Level level, Entity entity, InteractionHand hand) {
        return false;
    }
    
    /**
     * Returns true if the player can cast the spells.
     * <p>
     * A player can cast a spells if the player's class type can cast spells and the player has enough mana to cast the spells.
     * @param level The level in which the spells is cast.
     * @param player The player that casts the spells.
     * @param hand The hand in which the spells is cast.
     * @see ClassType#canCastSpells()
     * @see ModCapabilities#getMana(LivingEntity)
     */
    protected boolean canCast(Level level, Player player, InteractionHand hand) {
        if (casterClassType != null) {
            if (casterClassType.equals(PlayerDataCore.getPlayerClassType(player)))
                return true;
            if (ModCapabilities.getMana(player).isPresent()) {
                return ModCapabilities.getMana(player).orElseThrow(NullPointerException::new).getCurrentMana() >= this.getManaCost();
            }
            return PlayerDataCore.getPlayerClassType(player).canCastSpells();
        }
        else return true;
        
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
