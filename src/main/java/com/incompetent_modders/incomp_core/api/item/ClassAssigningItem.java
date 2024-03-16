package com.incompetent_modders.incomp_core.api.item;

import com.incompetent_modders.incomp_core.ClientUtil;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class ClassAssigningItem extends Item {
    private final ResourceLocation classType;
    public ClassAssigningItem(Properties properties, ResourceLocation classType) {
        super(properties);
        this.classType = classType;
    }
    public ClassType getClassType() {
        return ModRegistries.CLASS_TYPE.get(classType);
    }
    public ClassType getClassType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return ModClassTypes.SIMPLE_HUMAN.get();
        }
        if (tag.contains("classType")) {
            String classTypeModid = tag.getString("classType").split(":")[0];
            String classTypeName = tag.getString("classType").split(":")[1];
            return ModRegistries.CLASS_TYPE.get(new ResourceLocation(classTypeModid, classTypeName));
        } else {
            return ModClassTypes.SIMPLE_HUMAN.get();
        }
    }
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (PlayerDataCore.getPlayerClassType(player) == getClassType(itemstack)) {
                return InteractionResultHolder.fail(itemstack);
            } else {
                PlayerDataCore.setPlayerClassType(player, getClassType(itemstack));
                Minecraft.getInstance().particleEngine.createTrackingEmitter(player, ParticleTypes.TOTEM_OF_UNDYING, 10);
                Minecraft.getInstance().particleEngine.createTrackingEmitter(player, ParticleTypes.ENCHANT, 10);
                Minecraft.getInstance().particleEngine.createTrackingEmitter(player, ParticleTypes.SCULK_SOUL, 10);
                return InteractionResultHolder.success(itemstack);
            }
        }
        return InteractionResultHolder.fail(itemstack);
    }
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(getClassType(stack).getDisplayName());
    }
    
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        CompoundTag tag = stack.getOrCreateTag();
        
        if (!tag.contains("classType")) {
            tag.putString("classType", getClassType().getClassTypeIdentifier().toString());
        }
    }
}
