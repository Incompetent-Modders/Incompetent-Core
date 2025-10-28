package com.incompetent_modders.incomp_core.mixin;

import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.core.def.attributes.species.RestrictArmorAttribute;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.ArmorSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorSlot.class)
public class ArmorSlotMixin {
    @Shadow
    @Final
    private LivingEntity owner;

    @Shadow @Final private EquipmentSlot slot;

    @ModifyReturnValue(method = "mayPlace", at = @At("RETURN"))
    private boolean preventArmorEquip(boolean original, ItemStack stack) {
        SpeciesType speciesType = PlayerDataHelper.getSpeciesType(this.owner);
        boolean canEquip = true;
        if (speciesType != null) {
            RestrictArmorAttribute restrictArmor = speciesType.get(ModSpeciesAttributes.RESTRICT_ARMOR.get());
            if (restrictArmor != null) {
                canEquip = restrictArmor.canEquip(this.owner, stack, slot);
            }
        }
        return original && canEquip;
    }
}
