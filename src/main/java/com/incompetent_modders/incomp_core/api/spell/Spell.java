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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class Spell {
    private final boolean isRangedAttack;
    private final int manaCost;
    private final int drawTime;
    private final int coolDown;
    private final SpellCategory category;
    private final ClassType casterClassType;
    @Nullable
    private String descriptionId;
    public Spell(boolean isRangedAttack, int manaCost, int drawTime, int coolDown, SpellCategory category, ClassType casterClassType) {
        this.isRangedAttack = isRangedAttack;
        this.manaCost = manaCost;
        this.drawTime = drawTime;
        this.coolDown = coolDown;
        this.category = category;
        this.casterClassType = casterClassType;
    }
    public final Spell getSpell(ResourceLocation rl) {
        if (rl.equals(this.getSpellIdentifier())) {
            return this;
        } else {
            IncompCore.LOGGER.error("Spell " + rl + " does not exist!");
            return null;
        }
    }
    
    public SpellCategory getCategory() {
        return category;
    }
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
    public ClassType getCasterClassType() {
        return casterClassType;
    }
    public ResourceLocation getSpellIconLocation() {
        return new ResourceLocation(this.getSpellIdentifier().getNamespace(), "textures/spells/" + this.getSpellIdentifier().getPath());
    }
    public int getDrawTime() {
        return drawTime;
    }
    public int getCoolDown() {
        return coolDown;
    }
    
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("spell", ModRegistries.SPELL.getKey(this));
        }
        
        return this.descriptionId;
    }
    
    public Component getDisplayName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }
    
    public ResourceLocation getSpellIdentifier() {
        return ModRegistries.SPELL.getKey(this);
    }
    public int getManaCost() {
        return manaCost;
    }
    public boolean isRangedAttack() {
        return isRangedAttack;
    }
    protected void onCast(Level level, Player player, InteractionHand hand) {
        CommonUtils.onCastEvent(level, player, hand);
        SpellUtils.removeMana(player, this.getManaCost());
    }
    protected void onCast(Level level, Entity entity, InteractionHand hand) {
        CommonUtils.onEntityCastEvent(level, entity, hand);
    }
    protected void onFail(Level level, Player player, InteractionHand hand) {
        SpellUtils.removeMana(player, this.getManaCost() / 2);
    }
    protected void onFail(Level level, Entity entity, InteractionHand hand) {
    
    }
    public void cast(Level level, Entity entity, InteractionHand hand) {
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
    protected boolean shouldFail(Level level, Player player, InteractionHand hand) {
        return false;
    }
    protected boolean shouldFail(Level level, Entity entity, InteractionHand hand) {
        return false;
    }
    protected boolean canCast(Level level, Player player, InteractionHand hand) {
        if (!PlayerDataCore.getPlayerClassType(player).canCastSpells())
            return false;
        if (ModCapabilities.getMana(player).isPresent()) {
            return ModCapabilities.getMana(player).orElseThrow(NullPointerException::new).getCurrentMana() >= this.getManaCost();
        } else {
            return false;
        }
    }
    protected void onHit(Level level, Player player) {
    }
    
    protected void doNothing(Level level, Player player) {
        player.displayClientMessage(Component.translatable("spell.spellcasting.do_nothing"), true);
    }
}
