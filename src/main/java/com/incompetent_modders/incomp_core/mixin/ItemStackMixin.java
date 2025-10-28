package com.incompetent_modders.incomp_core.mixin;

import com.incompetent_modders.incomp_core.core.def.attributes.PreventItemUse;
import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"))
    private InteractionResultHolder<ItemStack> onItemUse(Item instance, Level level, Player player, InteractionHand usedHand, Operation<InteractionResultHolder<ItemStack>> original) {
        ItemStack thisAsStack = (ItemStack) (Object) this;
        SpeciesType speciesType = PlayerDataHelper.getSpeciesType(player);
        InteractionResultHolder<ItemStack> action = original.call(thisAsStack.getItem(), level, player, usedHand);
        if (speciesType != null) {
            for (PreventItemUse preventUse : speciesType.getOfType(PreventItemUse.class)) {
                if (!preventUse.canUseItem(player, thisAsStack)) {
                    return InteractionResultHolder.fail(thisAsStack);
                }
            }
        }
        return action;
    }

}
