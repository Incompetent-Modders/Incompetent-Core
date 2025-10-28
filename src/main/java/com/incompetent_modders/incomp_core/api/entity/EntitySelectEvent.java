package com.incompetent_modders.incomp_core.api.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.LogicalSide;

public class EntitySelectEvent extends Event {
    
    public enum Type {
        SELECT, DESELECT;
    }
    public final Type type;
    public final LogicalSide side;
    public final Entity entity;
    public EntitySelectEvent(Type type, LogicalSide side , Entity entity) {
        this.type = type;
        this.side = side;
        this.entity = entity;
    }
    
    public static class SelectEvent extends EntitySelectEvent {
        public final Level level;
        public SelectEvent(Entity entity, Level level) {
            super(Type.SELECT, LogicalSide.SERVER, entity);
            this.level = level;
        }
        public Level getLevel() {
            return level;
        }
        
        public Entity getEntity() {
            return entity;
        }
    }
    
    public static class DeselectEvent extends EntitySelectEvent {
        public final Level level;
        public DeselectEvent(Entity entity, Level level) {
            super(Type.DESELECT, LogicalSide.SERVER, entity);
            this.level = level;
        }
        
        public Level getLevel() {
            return level;
        }
        
        public Entity getEntity() {
            return entity;
        }
    }
    
    
}
