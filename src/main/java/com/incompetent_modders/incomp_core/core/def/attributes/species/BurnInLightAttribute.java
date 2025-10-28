package com.incompetent_modders.incomp_core.core.def.attributes.species;

import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BurnInLightAttribute extends SpeciesAttribute implements SpeciesTickable {
    public static final Codec<BurnInLightAttribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ArmorSlot.CODEC.listOf().optionalFieldOf("damageable_armour", List.of())
                    .forGetter(att -> att.damageArmorSlots),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("ignition_seconds", 8)
                    .forGetter(att -> att.ignitionSeconds),
            ExtraCodecs.POSITIVE_INT.fieldOf("min_light")
                    .forGetter(att -> att.minLight),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_light", 15)
                    .forGetter(att -> att.maxLight)
    ).apply(instance, BurnInLightAttribute::new));

    public final List<ArmorSlot> damageArmorSlots;
    public final int ignitionSeconds;
    public final int minLight;
    public final int maxLight;

    public BurnInLightAttribute(List<ArmorSlot> damageArmorSlots, int ignitionSeconds, int minLight, int maxLight) {
        this.damageArmorSlots = damageArmorSlots;
        this.ignitionSeconds = ignitionSeconds;
        this.minLight = minLight;
        this.maxLight = maxLight;
    }


    @Override
    public void tick(Level level, LivingEntity entity) {
        if (entity.isSpectator() || (entity instanceof Player player && player.isCreative())) {
            return;
        }
        if (entity.isAlive()) {
            boolean doBurn = this.isBurnTick(entity);
            for (ArmorSlot armorSlot : this.damageArmorSlots) {
                ItemStack armorStack = entity.getItemBySlot(armorSlot.slot);
                if (!armorStack.isEmpty()) {
                    if (armorStack.isDamageableItem()) {
                        armorStack.setDamageValue(armorStack.getDamageValue() + entity.getRandom().nextInt(2));
                        if (armorStack.getDamageValue() >= armorStack.getMaxDamage()) {
                            entity.onEquippedItemBroken(armorStack.getItem(), EquipmentSlot.HEAD);
                            entity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                }
            }
            boolean fullCoverage = this.damageArmorSlots.stream().map(s -> entity.getItemBySlot(s.slot)).noneMatch(ItemStack::isEmpty);
            if (fullCoverage) {
                doBurn = false;
            }
            if (doBurn) {
                entity.igniteForSeconds(this.ignitionSeconds);
            }
        }
    }

    protected boolean isBurnTick(LivingEntity entity) {
        if (!entity.level().isClientSide) {
            BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
            int brightness = entity.level().getMaxLocalRawBrightness(blockpos);
            boolean flag = entity.isInWaterRainOrBubble() || entity.isInPowderSnow || entity.wasInPowderSnow;
            return brightness >= minLight && brightness <= maxLight && !flag;
        }

        return false;
    }

    @Override
    public SpeciesAttributeType<? extends SpeciesAttribute> getType() {
        return ModSpeciesAttributes.BURN_IN_LIGHT.get();
    }

    @SuppressWarnings("deprecation")
    public enum ArmorSlot implements StringRepresentable {
        HEAD(EquipmentSlot.HEAD),
        CHEST(EquipmentSlot.CHEST),
        LEGS(EquipmentSlot.LEGS),
        FEET(EquipmentSlot.FEET)
        ;

        public final EquipmentSlot slot;
        ArmorSlot(EquipmentSlot slot) {
            this.slot = slot;
        }

        public static final EnumCodec<ArmorSlot> CODEC = StringRepresentable.fromEnum(ArmorSlot::values);

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
