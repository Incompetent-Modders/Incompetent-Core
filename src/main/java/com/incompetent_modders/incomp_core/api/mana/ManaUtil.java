package com.incompetent_modders.incomp_core.api.mana;

import com.incompetent_modders.incomp_core.Config;
import com.incompetent_modders.incomp_core.registry.ModAttributes;
import com.incompetent_modders.incomp_core.registry.ModCapabilities;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;

import java.util.UUID;

public class ManaUtil {
    
    public static double getCurrentMana(LivingEntity e) {
        IManaCap mana = ModCapabilities.getMana(e).orElse(null);
        if (mana == null)
            return 0;
        return mana.getCurrentMana();
    }
    public record Mana(int Max, float Reserve) {
        //Usable max mana
        public int getRealMax() {
            return (int) (Max * (1.0 - Reserve));
        }
    }
    
    // UUIDs for the configurable bonus on mana attributes, to include them in multiplier calculations.
    // Only updated if the value changes.
    static final UUID MAX_MANA_MODIFIER = UUID.fromString("71e00672-51f3-43b3-92cb-166496ef38e1");
    static final UUID MANA_REGEN_MODIFIER = UUID.fromString("bbb182cc-0d9c-402c-9d00-3adb19722e7b");
    
    
    // Calculate Max Mana & Mana Reserve to keep track of the mana reserved by familiars & co.
    public static Mana calcMaxMana(Player e) {
        IManaCap mana = ModCapabilities.getMana(e).orElse(null);
        if (mana == null)
            return new Mana(0, 0f);
        
        double rawMax = 0;
        for (ItemStack stack : e.getArmorSlots()) {
            if (stack.getItem() instanceof IManaArmor) {
                rawMax += ((IManaArmor) stack.getItem()).getMaxManaBoost(stack);
            }
        }
        rawMax += Config.maxMana;
        
        var manaAttribute = e.getAttribute(ModAttributes.MAX_MANA.get());
        if (manaAttribute != null) {
            var manaCache = manaAttribute.getModifier(MAX_MANA_MODIFIER);
            if (manaCache == null || manaCache.getAmount() != rawMax) {
                if (manaCache != null) manaAttribute.removeModifier(manaCache.getId());
                manaAttribute.addTransientModifier(new AttributeModifier(MAX_MANA_MODIFIER, "Mana Cache", rawMax, AttributeModifier.Operation.ADDITION));
            }
            rawMax = manaAttribute.getValue();
        }
        
        int max = (int) rawMax;
        
        MaxManaCalcEvent event = new MaxManaCalcEvent(e, max);
        NeoForge.EVENT_BUS.post(event);
        max = event.getMax();
        float reserve = event.getReserve();
        return new Mana(max, reserve);
    }
    
    //Returns the max mana of the player, not including the mana reserved by familiars & co.
    public static int getMaxMana(Player e) {
        return calcMaxMana(e).getRealMax();
    }
    
    public static double getManaRegen(Player e) {
        IManaCap mana = ModCapabilities.getMana(e).orElse(null);
        
        if (mana == null) return 0;
        double regen = 0;
        
        for (ItemStack stack : e.getArmorSlots()) {
            if (stack.getItem() instanceof IManaArmor) {
                regen += ((IManaArmor) stack.getItem()).getManaRegenBoost(stack);
            }
        }
        
        var manaAttribute = e.getAttribute(ModAttributes.MANA_REGEN.get());
        if (manaAttribute != null) {
            var manaCache = manaAttribute.getModifier(MANA_REGEN_MODIFIER);
            if (manaCache == null || manaCache.getAmount() != regen) {
                if (manaCache != null) manaAttribute.removeModifier(manaCache.getId());
                manaAttribute.addTransientModifier(new AttributeModifier(MANA_REGEN_MODIFIER, "Mana Regen Cache", regen, AttributeModifier.Operation.ADDITION));
            }
            regen = manaAttribute.getValue();
        }
        
        ManaRegenCalcEvent event = new ManaRegenCalcEvent(e, regen);
        NeoForge.EVENT_BUS.post(event);
        regen = event.getRegen();
        return regen;
    }
}
