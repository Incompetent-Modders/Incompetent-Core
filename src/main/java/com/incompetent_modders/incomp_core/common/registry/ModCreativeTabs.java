package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.annotations.HasOwnTab;
import com.incompetent_modders.incomp_core.api.item.ItemSpellSlots;
import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.incompetent_modders.incomp_core.core.def.Spell;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.BiConsumer;
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

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> UTIL_CREATIVE_TAB = REGISTER.register("utility", id -> {
        final CreativeModeTab.Builder builder = CreativeModeTab.builder();
        builder.title(Component.translatable(id.toLanguageKey("itemGroup")))
                .icon(() -> new ItemStack(ModItems.ASSIGN_CLASS))
                .withSearchBar()
                .displayItems((pParameters, pOutput) -> {
                    pParameters.holders()
                            .lookup(ModRegistries.Keys.SPELL)
                            .ifPresent(
                                    registryLookup -> {
                                        generateSpellTomes(pOutput, registryLookup, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                                    });
                    pParameters.holders()
                            .lookup(Registries.MOB_EFFECT)
                            .ifPresent(
                                    registryLookup -> {
                                        generateEffectPostponeItems(pOutput, registryLookup, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                                    });
                    pParameters.holders()
                            .lookup(ModRegistries.Keys.SPECIES_TYPE)
                            .ifPresent(
                                    registryLookup -> {
                                        generateSpeciesItems(pOutput, registryLookup, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                                    });
                    pParameters.holders()
                            .lookup(ModRegistries.Keys.CLASS_TYPE)
                            .ifPresent(
                                    registryLookup -> {
                                        generateClassItems(pOutput, registryLookup, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                                    });
                })
                .withTabsBefore(BASE_CREATIVE_TAB.getId());
        return builder.build();
    });
    
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
    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerTabSearchBar(String name, Holder<Item> icon, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> displayItems, Consumer<CreativeModeTab.Builder> additionalProperties) {
        return REGISTER.register(name, id -> {
            final CreativeModeTab.Builder builder = CreativeModeTab.builder();
            builder.title(Component.translatable(id.toLanguageKey("itemGroup")))
                    .icon(() -> new ItemStack(icon))
                    .withSearchBar()
                    .displayItems((pParameters, pOutput) -> displayItems.accept(pParameters, pOutput));
            additionalProperties.accept(builder);
            return builder.build();
        });
    }

    private static void generateSpellTomes(CreativeModeTab.Output output, HolderLookup<Spell> spells, CreativeModeTab.TabVisibility tabVisibility) {
        spells.listElements().forEach((spell) -> {
            ItemStack stack = new ItemStack(ModItems.SPELL_TOME);
            ItemSpellSlots.Entry entry = new ItemSpellSlots.Entry(spell.getKey(), 0);
            ItemSpellSlots spellSlots = new ItemSpellSlots(List.of(entry), 1, 0);
            stack.set(ModDataComponents.SPELLS, spellSlots);
            output.accept(stack, tabVisibility);
        });
    }

    private static void generateEffectPostponeItems(CreativeModeTab.Output output, HolderLookup<MobEffect> effects, CreativeModeTab.TabVisibility tabVisibility) {
        effects.listElements().filter(effect -> !effect.value().isInstantenous()).forEach((effect) -> {
            ItemStack stack = new ItemStack(ModItems.EFFECT_POSTPONE);
            stack.set(ModDataComponents.STORED_EFFECT_POSTPONE, effect.getKey());
            output.accept(stack, tabVisibility);
        });
    }
    private static void generateSpeciesItems(CreativeModeTab.Output output, HolderLookup<SpeciesType> speciesTypes, CreativeModeTab.TabVisibility tabVisibility) {
        speciesTypes.listElements().forEach((species) -> {
            ItemStack stack = new ItemStack(ModItems.ASSIGN_SPECIES);
            stack.set(ModDataComponents.STORED_SPECIES_TYPE, species.getKey());
            output.accept(stack, tabVisibility);
        });
    }
    private static void generateClassItems(CreativeModeTab.Output output, HolderLookup<ClassType> classTypes, CreativeModeTab.TabVisibility visibility) {
        classTypes.listElements().forEach((classType) -> {
            ItemStack stack = new ItemStack(ModItems.ASSIGN_CLASS);
            stack.set(ModDataComponents.STORED_CLASS_TYPE, classType.getKey());
            output.accept(stack, visibility);
        });
    }
    
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
