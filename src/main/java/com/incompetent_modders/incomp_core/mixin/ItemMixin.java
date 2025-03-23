package com.incompetent_modders.incomp_core.mixin;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.item.ItemSpellSlots;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.client.util.ClientUtil;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.incompetent_modders.incomp_core.core.def.Spell;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.incompetent_modders.incomp_core.common.util.Utils.opposite;

@SuppressWarnings("ConstantValue")
@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack usedStack = player.getItemInHand(usedHand);
        if (incompetent_Core$canCastSpells(usedStack)) {
            ItemStack opposite = player.getItemInHand(opposite(usedHand));
            Spell selectedSpell = CastingItemUtil.getSelectedSpellInstance(usedStack, player);
            boolean noCastProgress = SpellCastingItem.getCastProgress(usedStack) == 0;
            boolean isBlankSpell = selectedSpell.isBlankSpell();
            ItemStack catalyst = selectedSpell.definition().conditions().catalyst().item();
            if (isBlankSpell) {
                IncompCore.LOGGER.info("No spell selected.");
                cir.setReturnValue(InteractionResultHolder.fail(usedStack));
            }
            if (!catalyst.isEmpty()) {
                if (!opposite.equals(catalyst)) {
                    IncompCore.LOGGER.info("Spell needs catalyst");
                    cir.setReturnValue(InteractionResultHolder.fail(usedStack));
                }
                incompetent_Core$startCasting(player, usedHand, usedStack, selectedSpell, noCastProgress);
            }
            incompetent_Core$startCasting(player, usedHand, usedStack, selectedSpell, noCastProgress);
        }
    }
    @Inject(method = "onUseTick", at = @At("HEAD"))
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration, CallbackInfo ci) {
        if (stack.getOrDefault(ModDataComponents.REMAINING_DRAW_TIME, 0) > 0) {
            incompetent_Core$decrementCastProgress(stack);
        }
        boolean casting = stack.getOrDefault(ModDataComponents.CASTING, false);
        if (stack.getOrDefault(ModDataComponents.REMAINING_DRAW_TIME, 0) == 0 && !casting) {
            Spell spell = CastingItemUtil.getSelectedSpellInstanceWithKey(stack, livingEntity).getSecond();
            if (spell != null && !casting) {
                stack.set(ModDataComponents.CASTING, true);
                if (livingEntity instanceof Player player) {
                    IncompCore.LOGGER.info("Executing spell: {}", Spell.getDisplayName(CastingItemUtil.getSelectedSpellKey(stack, livingEntity)).getString());
                    spell.executeCast(player);
                    player.getCooldowns().addCooldown(stack.getItem(), CastingItemUtil.getSelectedSpellInstance(stack, livingEntity).definition().drawTime());
                    stack.set(ModDataComponents.CASTING, false);
                }
            }
            IncompCore.LOGGER.info("Casting has finished.");
            livingEntity.stopUsingItem();
        }
    }
    @Inject(method = "appendHoverText", at = @At("HEAD"))
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (incompetent_Core$canCastSpells(stack)) {
            HolderLookup.Provider provider = context.registries();
            if (provider != null) {
                ClientUtil.createSelectedSpellTooltip(tooltip, stack, provider);
            }
            if (stack.getOrDefault(ModDataComponents.MAX_SPELL_SLOTS, 0) > 1) {
                ClientUtil.createAvailableSpellsTooltip(tooltip, stack);
            }
        }
    }
    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    public void getUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAnim> cir) {
        if (stack.has(ModDataComponents.SPELLS)) cir.setReturnValue(UseAnim.BOW);
    }
    @Inject(method = "releaseUsing", at = @At("HEAD"))
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged, CallbackInfo ci) {
        stack.set(ModDataComponents.REMAINING_DRAW_TIME, 0);
    }
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected, CallbackInfo ci) {
        if (stack.has(ModDataComponents.SPELLS)) {
            if (!stack.has(ModDataComponents.MAX_SPELL_SLOTS)) {
                List<ItemSpellSlots.Entry> spells = stack.get(ModDataComponents.SPELLS).spells();
                stack.set(ModDataComponents.MAX_SPELL_SLOTS, spells.size());
            }
            if (!stack.has(ModDataComponents.SELECTED_SPELL_SLOT)) {
                stack.set(ModDataComponents.SELECTED_SPELL_SLOT, 0);
            }
            if (!stack.has(ModDataComponents.REMAINING_DRAW_TIME) && entity instanceof LivingEntity livingEntity) {
                stack.set(ModDataComponents.REMAINING_DRAW_TIME, CastingItemUtil.getSelectedSpellInstance(stack, livingEntity).definition().drawTime());
            }
        }

    }
    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    public void getUseDuration(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (stack.has(ModDataComponents.SPELLS)) {
            cir.setReturnValue(CastingItemUtil.getSelectedSpellInstance(stack, entity).definition().drawTime());
        }
    }

    @Unique
    private void incompetent_Core$startCasting(Player player, InteractionHand usedHand, ItemStack usedStack, Spell selectedSpell, boolean noCastProgress) {
        if (!noCastProgress) {
            IncompCore.LOGGER.info("Starting cast progress.");
            SpellCastingItem.setCastProgress(usedStack, player);
            player.getCooldowns().addCooldown(usedStack.getItem(), selectedSpell.definition().drawTime());
        } else  {
            player.startUsingItem(usedHand);
            IncompCore.LOGGER.info("Player has selected spell: {}", Spell.getDisplayName(CastingItemUtil.getSelectedSpellKey(usedStack, player)).getString());
            selectedSpell.executeCast(player);
        }
    }

    @Unique
    private void incompetent_Core$decrementCastProgress(ItemStack stack) {
        DataComponentMap components = stack.getComponents();
        int ticks = components.getOrDefault(ModDataComponents.REMAINING_DRAW_TIME.get(), 0);
        if (ticks > 0) {
            IncompCore.LOGGER.info("Decrementing cast progress for {}", stack);
            stack.set(ModDataComponents.REMAINING_DRAW_TIME, ticks - 1);
        }
    }

    @Unique
    private boolean incompetent_Core$canCastSpells(ItemStack stack) {
        return stack.has(ModDataComponents.SPELLS);
    }
}
