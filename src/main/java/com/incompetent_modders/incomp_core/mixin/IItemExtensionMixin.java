package com.incompetent_modders.incomp_core.mixin;

import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.core.def.attributes.species.RestrictArmorAttribute;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IItemExtension.class)
public interface IItemExtensionMixin {

    @Inject(method = "canEquip(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"), cancellable = true)
    default void canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        SpeciesType speciesType = PlayerDataHelper.getSpeciesType(entity);
        if (speciesType != null) {
            RestrictArmorAttribute restrictArmor = speciesType.get(ModSpeciesAttributes.RESTRICT_ARMOR.get());
            if (restrictArmor != null) {
                if (!restrictArmor.canEquip(entity, stack, armorType))
                    cir.setReturnValue(false);
            }
        }
    }
}
