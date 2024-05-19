package com.incompetent_modders.incomp_core.api.item;

import com.incompetent_modders.incomp_core.api.annotations.HasOwnTab;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import com.incompetent_modders.incomp_core.util.ModDataComponents;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
public class SpeciesAssigningItem extends Item {
    private final ResourceLocation speciesType;
    public SpeciesAssigningItem(Properties properties, ResourceLocation speciesType) {
        super(properties);
        this.speciesType = speciesType;
    }
    public ResourceLocation getSpeciesType() {
        return speciesType;
    }
    public ResourceLocation getSpeciesType(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.STORED_SPECIES_TYPE.get(), CommonUtils.defaultSpecies);
    }
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (PlayerDataCore.SpeciesData.getSpecies(player) == getSpeciesType(itemstack)) {
                return InteractionResultHolder.fail(itemstack);
            } else {
                PlayerDataCore.SpeciesData.setSpecies(player, getSpeciesType(itemstack));
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
        tooltip.add(SpeciesListener.getDisplayName(getSpeciesType(stack)));
    }
    
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        if (!stack.has(ModDataComponents.STORED_SPECIES_TYPE.get())) {
            stack.set(ModDataComponents.STORED_SPECIES_TYPE.get(), getSpeciesType());
        }
    }
    @Override
    public String getDescriptionId(ItemStack stack) {
        return "item.incompetent_core.assign_class." + getSpeciesType(stack).getNamespace() + "." + getSpeciesType(stack).getPath();
    }
}
