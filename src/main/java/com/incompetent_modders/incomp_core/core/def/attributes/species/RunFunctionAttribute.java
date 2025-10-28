package com.incompetent_modders.incomp_core.core.def.attributes.species;

import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttribute;
import com.incompetent_modders.incomp_core.api.species.attribute.SpeciesAttributeType;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class RunFunctionAttribute extends SpeciesAttribute implements SpeciesTickable {

    public static final Codec<RunFunctionAttribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CacheableFunction.CODEC.fieldOf("function").forGetter(att -> att.function)
    ).apply(instance, RunFunctionAttribute::new));

    private final CacheableFunction function;

    public RunFunctionAttribute(CacheableFunction function) {
        this.function = function;
    }

    @Override
    public void tick(Level level, LivingEntity entity) {
        MinecraftServer minecraftserver = entity.getServer();
        if (minecraftserver != null) {
            Optional<CommandFunction<CommandSourceStack>> commandFunc = function.get(minecraftserver.getFunctions());
            commandFunc.ifPresent((commandFunction) ->
                    minecraftserver.getFunctions()
                            .execute(commandFunction, entity.createCommandSourceStack().withEntity(entity).withSuppressedOutput()
                                    .withPermission(2)));
        }
    }

    @Override
    public SpeciesAttributeType<? extends SpeciesAttribute> getType() {
        return ModSpeciesAttributes.RUN_FUNCTION.get();
    }
}
