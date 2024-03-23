package com.incompetent_modders.incomp_core.api.entity.projectile;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.registry.dev.DevEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class SpellProjectile extends AbstractHurtingProjectile {
    public static final EntityDataSerializer<ResourceLocation> RESOURCE_LOCATION = EntityDataSerializer.simple(FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation);
    private static final EntityDataAccessor<Float> DATA_GRAVITY = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_EXPLOSIVE_POWER = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ResourceLocation> DATA_SPELL = SynchedEntityData.defineId(SpellProjectile.class, RESOURCE_LOCATION);
    
    public SpellProjectile(EntityType<? extends SpellProjectile> type, Level level) {
        super(type, level);
    }
    public SpellProjectile(LivingEntity entity, Level level) {
        super(DevEntities.SPELL_PROJECTILE.get(), level);
        this.setOwner(entity);
    }
    public SpellProjectile(Level level, double x, double y, double z) {
        super(DevEntities.SPELL_PROJECTILE.get(), level);
        this.setPos(x, y, z);
    }
    
    public void setDataGravity(float gravity) {
        this.entityData.set(DATA_GRAVITY, gravity);
    }
    public void setDataColor(int color) {
        this.entityData.set(DATA_COLOR, color);
    }
    public void setDataSpell(ResourceLocation spell) {
        this.entityData.set(DATA_SPELL, spell);
    }
    public float getGravity() {
        float gravity;
        if (this.entityData.get(DATA_GRAVITY) == null) {
            gravity = 0.0F;
        } else {
            gravity = this.entityData.get(DATA_GRAVITY);
        }
        return gravity;
    }
    public int getColorInt() {
        int color;
        if (this.entityData.get(DATA_COLOR) == null) {
            color = 0xFFFFFF;
        } else {
            color = this.entityData.get(DATA_COLOR);
        }
        return color;
    }
    public Color getColor() {
        return HexToColor(Integer.toHexString(getColorInt()));
    }
    public ResourceLocation getSpellResourceLoc() {
        ResourceLocation spell;
        if (this.entityData.get(DATA_SPELL) == null) {
            spell = new ResourceLocation("incompetent_core:empty_spell");
        } else {
            spell = this.entityData.get(DATA_SPELL);
        }
        return spell;
    }
    public Spell getSpell() {
        return ModRegistries.SPELL.get(getSpellResourceLoc());
    }
    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_GRAVITY, getGravity());
        this.getEntityData().define(DATA_COLOR, getColorInt());
        this.getEntityData().define(DATA_SPELL, getSpellResourceLoc());
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        float gravity = this.getGravity();
        int color = this.getColorInt();
        ResourceLocation spell = this.getSpellResourceLoc();
        compoundTag.putFloat("Gravity", gravity);
        compoundTag.putInt("Color", color);
        compoundTag.putString("Spell", spell.toString());
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        float gravity = compoundTag.getFloat("Gravity");
        int color = compoundTag.getInt("Color");
        ResourceLocation spell = new ResourceLocation(compoundTag.getString("Spell"));
        this.setDataGravity(gravity);
        this.setDataColor(color);
        this.setDataSpell(spell);
    }
    @Override
    protected AABB makeBoundingBox() {
        float f = this.getType().getDimensions().width / 2.0F;
        float f1 = this.getType().getDimensions().height;
        float f2 = 0.15F;
        return new AABB(
                this.position().x - (double)f,
                this.position().y - 0.15F,
                this.position().z - (double)f,
                this.position().x + (double)f,
                this.position().y - 0.15F + (double)f1,
                this.position().z + (double)f
        );
    }
    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            getSpell().onHit(this.level(), this, hitresult);
            
            this.checkInsideBlocks();
            Vec3 vec3 = this.getDeltaMovement();
            double d2 = this.getX() + vec3.x;
            double d0 = this.getY() + vec3.y;
            double d1 = this.getZ() + vec3.z;
            this.updateRotation();
            float f;
            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    float f1 = 0.25F;
                    this.level().addParticle(ParticleTypes.ELECTRIC_SPARK, d2 - vec3.x * 0.25, d0 - vec3.y * 0.25, d1 - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
                }
                
                f = 0.8F;
            } else {
                f = 0.99F;
            }
            
            this.setDeltaMovement(vec3.scale(f));
            if (!this.isNoGravity()) {
                Vec3 vec31 = this.getDeltaMovement();
                this.setDeltaMovement(vec31.x, vec31.y - (double) this.getGravity(), vec31.z);
            }
            
            this.setPos(d2, d0, d1);
        } else {
            this.spectacularFinish();
            this.discard();
        }
    }
    
    private void spectacularFinish() {
        Minecraft.getInstance().particleEngine.createTrackingEmitter(this, ParticleTypes.TOTEM_OF_UNDYING, 10);
        Minecraft.getInstance().particleEngine.createTrackingEmitter(this, ParticleTypes.ENCHANT, 10);
    }
    protected boolean shouldBurn() {
        return false;
    }
    public static Color HexToColor(String hex)
    {
        hex = hex.replace("#", "");
        switch (hex.length()) {
            case 6:
                return new Color(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16));
            case 8:
                return new Color(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16),
                        Integer.valueOf(hex.substring(6, 8), 16));
        }
        return null;
    }
    @Override
    public boolean shouldRenderAtSqrDistance(double p_36837_) {
        double d0 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d0)) {
            d0 = 4.0;
        }
        
        d0 *= 64.0;
        return p_36837_ < d0 * d0;
    }
    
    @Override
    public void onHit(HitResult result) {
        super.onHit(result);
        this.spectacularFinish();
        if (!this.level().isClientSide) {
            this.discard();
        }
    }
}
