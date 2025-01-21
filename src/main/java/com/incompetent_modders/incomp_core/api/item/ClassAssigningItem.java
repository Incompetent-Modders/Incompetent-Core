package com.incompetent_modders.incomp_core.api.item;

import com.incompetent_modders.incomp_core.api.annotations.HasOwnTab;
import com.incompetent_modders.incomp_core.common.util.Utils;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.incompetent_modders.incomp_core.core.def.ClassType;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@HasOwnTab
public class ClassAssigningItem extends Item {
    private final ResourceKey<ClassType> classType;
    public ClassAssigningItem(Properties properties, ResourceKey<ClassType> classType) {
        super(properties);
        this.classType = classType;
    }
    public ResourceKey<ClassType> getClassType() {
        return classType;
    }
    public ResourceKey<ClassType> getClassType(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.STORED_CLASS_TYPE.get(), Utils.defaultClass);
    }
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (PlayerDataHelper.getClassTypeWithKey(player).getFirst() == getClassType(itemstack)) {
                return InteractionResultHolder.fail(itemstack);
            } else {
                PlayerDataHelper.setClassType(player, getClassType(itemstack));
                Minecraft.getInstance().particleEngine.createTrackingEmitter(player, ParticleTypes.TOTEM_OF_UNDYING, 10);
                Minecraft.getInstance().particleEngine.createTrackingEmitter(player, ParticleTypes.ENCHANT, 10);
                Minecraft.getInstance().particleEngine.createTrackingEmitter(player, ParticleTypes.SCULK_SOUL, 10);
                return InteractionResultHolder.success(itemstack);
            }
        }
        return InteractionResultHolder.fail(itemstack);
    }
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(ClassType.getDisplayName(getClassType(stack).location()));
    }
    
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        if (!stack.has(ModDataComponents.STORED_CLASS_TYPE.get())) {
            stack.set(ModDataComponents.STORED_CLASS_TYPE.get(), getClassType());
        }
    }
    @Override
    public String getDescriptionId(ItemStack stack) {
        return "item.incompetent_core.assign_class." + getClassType(stack).location().getNamespace() + "." + getClassType(stack).location().getPath();
    }
}
