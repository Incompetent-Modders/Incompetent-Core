package com.incompetent_modders.incomp_core.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.annotations.HasOwnTab;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.util.ModDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public class ModCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IncompCore.MODID);
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BASE_CREATIVE_TAB = registerTab("base",
            ModItems.SPELL_TOME, output -> {
                for (DeferredHolder<Item, ? extends Item> registry : ModItems.getItems()) {
                    if (registry.get() instanceof BlockItem || registry.get().getClass().isAnnotationPresent(HasOwnTab.class)) continue;
                    output.accept(registry.get());
                }
            }, builder -> builder.withTabsBefore(CreativeModeTabs.SPAWN_EGGS));
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CLASS_CREATIVE_TAB = registerTabSearchBar("classes",
            ModItems.ASSIGN_CLASS, output -> {
                for (ResourceLocation classID : ClassTypeListener.getAllClassTypes()) {
                    Item item = ModItems.ASSIGN_CLASS.get();
                    ItemStack stack = new ItemStack(item);
                    stack.set(ModDataComponents.STORED_CLASS_TYPE, classID);
                    output.accept(stack);
                }
            }, builder -> builder.withTabsBefore(BASE_CREATIVE_TAB.getId()));
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPECIES_CREATIVE_TAB = registerTabSearchBar("species",
            ModItems.ASSIGN_SPECIES, output -> {
                for (ResourceLocation speciesID : SpeciesListener.getAllSpecies()) {
                    Item item = ModItems.ASSIGN_SPECIES.get();
                    ItemStack stack = new ItemStack(item);
                    stack.set(ModDataComponents.STORED_SPECIES_TYPE, speciesID);
                    output.accept(stack);
                }
            }, builder -> builder.withTabsBefore(CLASS_CREATIVE_TAB.getId()));
    
    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerTab(String name, Holder<Item> icon, Consumer<CreativeModeTab.Output> displayItems, Consumer<CreativeModeTab.Builder> additionalProperties) {
        return REGISTER.register(name, id -> {
            final CreativeModeTab.Builder builder = CreativeModeTab.builder();
            builder.title(Component.translatable(id.toLanguageKey("itemGroup")))
                    .icon(() -> new ItemStack(icon))
                    .displayItems((pParameters, pOutput) -> displayItems.accept(pOutput));
            additionalProperties.accept(builder);
            return builder.build();
        });
    }
    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerTabSearchBar(String name, Holder<Item> icon, Consumer<CreativeModeTab.Output> displayItems, Consumer<CreativeModeTab.Builder> additionalProperties) {
        return REGISTER.register(name, id -> {
            final CreativeModeTab.Builder builder = CreativeModeTab.builder();
            builder.title(Component.translatable(id.toLanguageKey("itemGroup")))
                    .icon(() -> new ItemStack(icon))
                    .withSearchBar()
                    .displayItems((pParameters, pOutput) -> displayItems.accept(pOutput));
            additionalProperties.accept(builder);
            return builder.build();
        });
    }
    
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
