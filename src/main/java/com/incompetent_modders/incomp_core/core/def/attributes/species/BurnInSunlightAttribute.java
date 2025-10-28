package com.incompetent_modders.incomp_core.core.def.attributes.species;

import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BurnInSunlightAttribute extends SpeciesAttribute implements SpeciesTickable {
    public static final Codec<BurnInSunlightAttribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("damage_helmets", true)
                    .forGetter(att -> att.damageHelmets),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("ignition_seconds", 8)
                    .forGetter(att -> att.ignitionSeconds)
    ).apply(instance, BurnInSunlightAttribute::new));

    private final boolean damageHelmets;
    private final int ignitionSeconds;

    public BurnInSunlightAttribute(boolean damageHelmets, int ignitionSeconds) {
        this.damageHelmets = damageHelmets;
        this.ignitionSeconds = ignitionSeconds;
    }

    public BurnInSunlightAttribute(int ignitionSeconds) {
        this(true, ignitionSeconds);
    }

    public BurnInSunlightAttribute(boolean damageHelmets) {
        this(damageHelmets, 8);
    }

    public BurnInSunlightAttribute() {
        this(true, 8);
    }

    @Override
    public void tick(Level level, LivingEntity entity) {
        ItemStack helmetStack = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (entity.isSpectator() || (entity instanceof Player player && player.isCreative())) {
            return;
        }
        if (entity.isAlive()) {
            boolean doBurn = this.isSunBurnTick(entity);
            if (doBurn) {
                if (!helmetStack.isEmpty()) {
                    if (helmetStack.isDamageableItem() && this.damageHelmets) {
                        helmetStack.setDamageValue(helmetStack.getDamageValue() + entity.getRandom().nextInt(2));
                        if (helmetStack.getDamageValue() >= helmetStack.getMaxDamage()) {
                            entity.onEquippedItemBroken(helmetStack.getItem(), EquipmentSlot.HEAD);
                            entity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    doBurn = false;
                }
                if (doBurn) {
                    entity.igniteForSeconds(this.ignitionSeconds);
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
    public SpeciesAttributeType<? extends SpeciesAttribute> getType() {
        return ModSpeciesAttributes.BURN_IN_SUNLIGHT.get();
    }
}
