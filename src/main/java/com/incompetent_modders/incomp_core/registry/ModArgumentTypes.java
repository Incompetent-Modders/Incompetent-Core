package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.command.arguments.SpellArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.incompetent_modders.incomp_core.IncompCore.MODID;

public class ModArgumentTypes {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARG_TYPE = DeferredRegister.create(
            Registries.COMMAND_ARGUMENT_TYPE, MODID
    );
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, SingletonArgumentInfo<SpellArgument>> SPELL_ARG = ARG_TYPE.register(
            "spell", () -> ArgumentTypeInfos.registerByClass(
                    SpellArgument.class, SingletonArgumentInfo.contextFree(SpellArgument::new)
            )
    );
    
    public static void register(IEventBus eventBus) {
        ARG_TYPE.register(eventBus);
    }
}
