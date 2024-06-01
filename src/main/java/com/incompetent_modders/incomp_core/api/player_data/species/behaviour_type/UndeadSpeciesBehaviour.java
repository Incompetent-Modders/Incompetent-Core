package com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type;

import com.incompetent_modders.incomp_core.common.registry.ModSpeciesBehaviourTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
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
    public void apply(Level level, Player player) {
        ItemStack itemstack = player.getItemBySlot(EquipmentSlot.HEAD);
        if (player.isCreative() || player.isSpectator() || !burnInSun) {
            return;
        }
        if (player.isAlive()) {
            boolean flag = this.isSunBurnTick(player);
            if (flag) {
                if (!itemstack.isEmpty()) {
                    if (itemstack.isDamageableItem()) {
                        itemstack.setDamageValue(itemstack.getDamageValue() + player.getRandom().nextInt(2));
                        if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                            player.broadcastBreakEvent(EquipmentSlot.HEAD);
                            player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    flag = false;
                }
                if (flag) {
                    player.igniteForSeconds(8);
                }
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    protected boolean isSunBurnTick(Player player) {
        if (player.level().isDay() && !player.level().isClientSide) {
            float f = player.getLightLevelDependentMagicValue();
            BlockPos blockpos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
            boolean flag = player.isInWaterRainOrBubble() || player.isInPowderSnow || player.wasInPowderSnow;
            return f > 0.5F && player.getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && player.level().canSeeSky(blockpos);
        }
        
        return false;
    }
    @Override
    public SpeciesBehaviourType<? extends SpeciesBehaviour> getType() {
        return ModSpeciesBehaviourTypes.UNDEAD.get();
    }
}
