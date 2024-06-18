package com.incompetent_modders.incomp_core.common.registry.content.passive_effects;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player.ClassData;
import com.incompetent_modders.incomp_core.api.player.SpeciesData;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffect;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffectType;
import com.incompetent_modders.incomp_core.common.registry.ModClassPassiveEffects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionChargesPassiveEffect extends ClassPassiveEffect {
    public static final MapCodec<FunctionChargesPassiveEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("max_charges").forGetter(FunctionChargesPassiveEffect::maxCharges),
            Codec.INT.fieldOf("recharge_time").forGetter(FunctionChargesPassiveEffect::rechargeTime)
    ).apply(instance, FunctionChargesPassiveEffect::new));
    
    private final int maxCharges;
    private final int rechargeTime;
    
    public FunctionChargesPassiveEffect(int maxCharges, int rechargeTime) {
        this.maxCharges = maxCharges;
        this.rechargeTime = rechargeTime;
    }
    
    public int maxCharges() {
        return maxCharges;
    }
    
    public int rechargeTime() {
        return rechargeTime;
    }
    
    
    @Override
    public void apply(Level level, ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains("AbilityCharges")) {
            data.putInt("AbilityCharges", maxCharges);
        }
        int remainingCharges = data.getInt("AbilityCharges");
        if (remainingCharges < maxCharges) {
            data.putInt("AbilityRechargeTicks", rechargeTime);
            int ticks = data.getInt("AbilityRechargeTicks");
            if (ticks >= 0) {
                data.putInt("AbilityRechargeTicks", ticks - 1);
            } else {
                data.putInt("AbilityCharges", remainingCharges + 1);
                updateChargePositions(player.position(), 1.0f, new ArrayList<>());
            }
        }
        renderChargeParticles(level, player);
    }
    
    @Override
    public ClassPassiveEffectType<? extends ClassPassiveEffect> getType() {
        return ModClassPassiveEffects.FUNCTION_CHARGES.get();
    }
    
    private void renderChargeParticles(Level level, ServerPlayer player) {
        Vec3 origin = player.position();
        CompoundTag data = player.getPersistentData();
        int remainingCharges = data.getInt("AbilityCharges");
        float deltaTime = level.getGameTime();
        List<Charge> charges = new ArrayList<>();
        for (int i = 0; i < remainingCharges; i++) {
            charges.add(new Charge());
        }
        for (Charge charge : charges) {
            float t = deltaTime * charge.speed;
            t = Math.min(t, 1.0f);
            if (t >= 1.0f) {
                charge.currentPos = charge.targetPos;
                charge.targetPos = new Vec3(0, 0, 0);
                charge.speed = 0.0f;
            }
            Vec3 chargePosition = lerp(charge.currentPos, charge.targetPos, t);
            for (int i = 0; i < 10; i++) {
                level.addAlwaysVisibleParticle(ParticleTypes.DRAGON_BREATH, chargePosition.x, origin.y + 1, chargePosition.z, 0.0, 0.0, 0.0);
            }
        }
    }
    void updateChargePositions(Vec3 pos, float orbitRadius, List<Charge> charges) {
        float angleIncrement = 360.0f / charges.size();
        for (int i = 0; i < charges.size(); i++) {
            float angle = angleIncrement * i;
            float x = (float) Math.cos(Math.toRadians(angle)) * orbitRadius;
            float z = (float) Math.sin(Math.toRadians(angle)) * orbitRadius;
            charges.get(i).targetPos = new Vec3(x, pos.y, z);
        }
        
    }
    Vec3 lerp(Vec3 start, Vec3 end, float t) {
        return start.add(end.subtract(start)).scale(t);
    }
    static class Charge {
        Vec3 currentPos = new Vec3(0, 0, 0);
        Vec3 targetPos = new Vec3(0, 0, 0);
        float speed = 0.3f;
    }
}
