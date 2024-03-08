package com.incompetent_modders.incomp_core.api.spell;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.LogicalSide;

public class SpellEvent extends Event {
    public enum Type {
        CAST, ENTITY_CAST, SPELL_SLOT_SCROLL;
    }
    public final Type type;
    public final LogicalSide side;
    
    public SpellEvent(Type type, LogicalSide side) {
        this.type = type;
        this.side = side;
    }
    
    public static class CastEvent extends SpellEvent {
        public final Level level;
        public final Player player;
        public final InteractionHand hand;
        public CastEvent(Level level, Player player, InteractionHand hand) {
            super(Type.CAST, LogicalSide.SERVER);
            this.level = level;
            this.player = player;
            this.hand = hand;
        }
        public Level getLevel() {return level;}
        public Player getPlayer() {return player;}
        public InteractionHand getHand() {return hand;}
        
    }
    
    public static class EntityCastEvent extends SpellEvent {
        public final Level level;
        public final Entity entity;
        public final InteractionHand hand;
        public EntityCastEvent(Level level, Entity entity, InteractionHand hand) {
            super(Type.ENTITY_CAST, LogicalSide.SERVER);
            this.level = level;
            this.entity = entity;
            this.hand = hand;
        }
        public Level getLevel() {return level;}
        public Entity getEntity() {return entity;}
        public InteractionHand getHand() {return hand;}
    }
    
    public static class SpellSlotScrollEvent extends SpellEvent {
        public final Level level;
        public final boolean up;
        public final Player player;
        public SpellSlotScrollEvent(Level level, boolean up, Player player) {
            super(Type.SPELL_SLOT_SCROLL, LogicalSide.SERVER);
            this.level = level;
            this.up = up;
            this.player = player;
        }
        public Level getLevel() {return level;}
        public boolean scrollingUp() {return up;}
        public Player getPlayer() {return player;}
    }
}
