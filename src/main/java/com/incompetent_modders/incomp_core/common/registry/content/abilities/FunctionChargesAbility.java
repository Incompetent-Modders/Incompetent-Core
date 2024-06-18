package com.incompetent_modders.incomp_core.common.registry.content.abilities;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player.ClassData;
import com.incompetent_modders.incomp_core.api.player.SpeciesData;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.Ability;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.common.registry.ModAbilities;
import com.incompetent_modders.incomp_core.common.registry.content.passive_effects.FunctionChargesPassiveEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class FunctionChargesAbility extends Ability {
    public static final MapCodec<FunctionChargesAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CacheableFunction.CODEC.optionalFieldOf("function").forGetter(FunctionChargesAbility::function)
    ).apply(instance, FunctionChargesAbility::new));
    
    private final Optional<CacheableFunction> function;
    
    public FunctionChargesAbility(Optional<CacheableFunction> function) {
        this.function = function;
    }
    public Optional<CacheableFunction> function() {
        return function;
    }
    
    @Override
    public void apply(Level level, Player player) {
        CompoundTag data = player.getPersistentData();
        if (!Objects.equals((ClassData.Get.passiveEffect(player)).getClassPassiveEffectTypeIdentifier(), ResourceLocation.fromNamespaceAndPath("incompetent_core", "function_charges"))) {
            IncompCore.LOGGER.error("Player does not have the FunctionCharges passive effect. This is required for the Function Charges ability to work. Please fix this in the {} class definition or the {} species definition", ClassData.Get.playerClassType(player), SpeciesData.Get.playerSpecies(player));
            return;
        }
        int remainingCharges = data.getInt("AbilityCharges");
        if (remainingCharges > 0) {
            MinecraftServer minecraftserver = player.getServer();
            if (minecraftserver != null) {
                data.putInt("AbilityCharges", remainingCharges - 1);
                this.function.flatMap((function) -> {
                    return function.get(player.getServer().getFunctions());
                }).ifPresent((commandFunction) -> {
                    player.getServer().getFunctions().execute(commandFunction, player.createCommandSourceStack().withEntity(player).withSuppressedOutput().withPermission(2));
                });
            }
        } else {
            player.displayClientMessage(Component.translatable("ability.incompetent_core.function_charges.no_charges"), true);
        }
    }
    
    @Override
    public AbilityType<? extends Ability> getType() {
        return ModAbilities.FUNCTION_CHARGES.get();
    }
}
