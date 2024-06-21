package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.annotations.HasOwnTab;
import com.incompetent_modders.incomp_core.api.item.ItemSpellSlots;
import com.incompetent_modders.incomp_core.client.managers.ClientClassTypeManager;
import com.incompetent_modders.incomp_core.client.managers.ClientSpeciesManager;
import com.incompetent_modders.incomp_core.client.managers.ClientSpellManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> UTIL_CREATIVE_TAB = registerTabSearchBar("utility",
            ModItems.ASSIGN_CLASS, output -> {
                generateEffectPostponeItems(output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                generateClassItems(output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                generateSpeciesItems(output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                generateSpellTomes(output, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }, builder -> builder.withTabsBefore(BASE_CREATIVE_TAB.getId()));
    
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
    
    private static void generateSpellTomes(CreativeModeTab.Output output, CreativeModeTab.TabVisibility visibility) {
        List<ResourceLocation> list = ClientSpellManager.getInstance().getSpellList().keySet().stream().toList();
        Set<ItemStack> set = ItemStackLinkedSet.createTypeAndComponentsSet();
        for (ResourceLocation resourceLocation : list) {
            ItemSpellSlots.Entry entry = new ItemSpellSlots.Entry(resourceLocation, 0);
            ItemSpellSlots spellSlots = new ItemSpellSlots(List.of());
            ItemStack itemstack = new ItemStack(ModItems.SPELL_TOME);
            itemstack.set(ModDataComponents.SPELLS, spellSlots.withSpellAdded(entry));
            itemstack.set(ModDataComponents.MAX_SPELL_SLOTS, 0);
            itemstack.set(ModDataComponents.SELECTED_SPELL_SLOT, 0);
            set.add(itemstack);
        }
        output.acceptAll(set, visibility);
    }
    private static void generateEffectPostponeItems(CreativeModeTab.Output output, CreativeModeTab.TabVisibility visibility) {
        Set<ItemStack> set = ItemStackLinkedSet.createTypeAndComponentsSet();
        for (ResourceLocation mobEffectID : BuiltInRegistries.MOB_EFFECT.keySet()) {
            Item item = ModItems.EFFECT_POSTPONE.get();
            ItemStack stack = new ItemStack(item);
            if (Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.get(mobEffectID)).isInstantenous()) continue;
            stack.set(ModDataComponents.STORED_EFFECT_POSTPONE, mobEffectID);
            set.add(stack);
        }
        output.acceptAll(set, visibility);
    }
    private static void generateSpeciesItems(CreativeModeTab.Output output, CreativeModeTab.TabVisibility visibility) {
        Set<ItemStack> set = ItemStackLinkedSet.createTypeAndComponentsSet();
        for (ResourceLocation speciesID : ClientSpeciesManager.getInstance().getSpeciesList()) {
            Item item = ModItems.ASSIGN_SPECIES.get();
            ItemStack stack = new ItemStack(item);
            stack.set(ModDataComponents.STORED_SPECIES_TYPE, speciesID);
            set.add(stack);
        }
        output.acceptAll(set, visibility);
    }
    private static void generateClassItems(CreativeModeTab.Output output, CreativeModeTab.TabVisibility visibility) {
        Set<ItemStack> set = ItemStackLinkedSet.createTypeAndComponentsSet();
        for (ResourceLocation classID : ClientClassTypeManager.getInstance().getClassTypeList()) {
            Item item = ModItems.ASSIGN_CLASS.get();
            ItemStack stack = new ItemStack(item);
            stack.set(ModDataComponents.STORED_CLASS_TYPE, classID);
            set.add(stack);
        }
        output.acceptAll(set, visibility);
    }
    
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
