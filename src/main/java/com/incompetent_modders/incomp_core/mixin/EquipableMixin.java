package com.incompetent_modders.incomp_core.mixin;

import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.core.def.attributes.species.RestrictArmorAttribute;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Equipable.class)
public interface EquipableMixin {

    @Inject(method = "swapWithEquipmentSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void preventArmorEquipping(Item item, Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, @Local ItemStack itemStack, @Local EquipmentSlot equipmentSlot) {
        SpeciesType speciesType = PlayerDataHelper.getSpeciesType(user);
        if (speciesType != null) {
            RestrictArmorAttribute restrictArmor = speciesType.get(ModSpeciesAttributes.RESTRICT_ARMOR.get());
            if (restrictArmor != null) {
                if (!restrictArmor.canEquip(user, itemStack, equipmentSlot)) {
                    cir.setReturnValue(InteractionResultHolder.fail(itemStack));
                }
            }
        }
    }
}
