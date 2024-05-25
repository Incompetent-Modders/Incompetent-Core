package com.incompetent_modders.incomp_core.registry.content.spell_results;

import com.incompetent_modders.incomp_core.api.spell.data.SpellResult;
import com.incompetent_modders.incomp_core.api.spell.data.SpellResultType;
import com.incompetent_modders.incomp_core.registry.ModSpellResultTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public class DamageCatalystResult extends SpellResult {
    public static final MapCodec<DamageCatalystResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("repair_amount").forGetter(DamageCatalystResult::getRepairAmount)
    ).apply(instance, DamageCatalystResult::new));
    
    private final int repairAmount;
    
    public DamageCatalystResult(int repairAmount) {
        this.repairAmount = repairAmount;
    }
    
    public int getRepairAmount() {
        return repairAmount;
    }
    
    @Override
    public void execute(Player player) {
        player.getOffhandItem().hurtAndBreak(getRepairAmount(), player, EquipmentSlot.OFFHAND);
    }
    
    @Override
    public SpellResultType<? extends SpellResult> getType() {
        return ModSpellResultTypes.DAMAGE_CATALYST.get();
    }
}
