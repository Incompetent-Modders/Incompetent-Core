package com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type;

import com.incompetent_modders.incomp_core.common.registry.ModSpeciesBehaviourTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UndeadSpeciesBehaviour extends SpeciesBehaviour {
    public static final MapCodec<UndeadSpeciesBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("burn_in_sun").forGetter(result -> {
                return result.burnInSun;
            })
    ).apply(instance, UndeadSpeciesBehaviour::new));
    
    private final boolean burnInSun;
    
    public UndeadSpeciesBehaviour(boolean burnInSun) {
        this.burnInSun = burnInSun;
    }
    
    @Override
    public void apply(Level level, LivingEntity entity) {
        ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (entity.isSpectator() || !burnInSun) {
            return;
        }
        if (entity instanceof Player player && player.isCreative()) {
            return;
        }
        if (entity.isAlive()) {
            boolean flag = this.isSunBurnTick(entity);
            if (flag) {
                if (!itemstack.isEmpty()) {
                    if (itemstack.isDamageableItem()) {
                        itemstack.setDamageValue(itemstack.getDamageValue() + entity.getRandom().nextInt(2));
                        if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                            entity.onEquippedItemBroken(itemstack.getItem(), EquipmentSlot.HEAD);
                            entity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    flag = false;
                }
                if (flag) {
                    entity.igniteForSeconds(8);
                }
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    protected boolean isSunBurnTick(LivingEntity entity) {
        if (entity.level().isDay() && !entity.level().isClientSide) {
            float f = entity.getLightLevelDependentMagicValue();
            BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
            boolean flag = entity.isInWaterRainOrBubble() || entity.isInPowderSnow || entity.wasInPowderSnow;
            return f > 0.5F && entity.getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && entity.level().canSeeSky(blockpos);
        }
        
        return false;
    }
    @Override
    public SpeciesBehaviourType<? extends SpeciesBehaviour> getType() {
        return ModSpeciesBehaviourTypes.UNDEAD.get();
    }
}
