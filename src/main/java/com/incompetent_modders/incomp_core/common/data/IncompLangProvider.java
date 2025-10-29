package com.incompetent_modders.incomp_core.common.data;

import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.common.registry.ModCreativeTabs;
import com.incompetent_modders.incomp_core.common.registry.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.NoSuchElementException;

public class IncompLangProvider extends LanguageProvider {
    public IncompLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }
    
    @Override
    protected void addTranslations() {
        this.item(ModItems.SPELL_TOME);
        this.item(ModItems.EFFECT_POSTPONE);
        this.item(ModItems.ASSIGN_CLASS);
        this.item(ModItems.ASSIGN_SPECIES);
        
        this.tab(ModCreativeTabs.BASE_CREATIVE_TAB);
        this.tab(ModCreativeTabs.UTIL_CREATIVE_TAB);
        
        this.castingTooltip("selected_spell", "Selected Spell:");
        this.castingTooltip("spell_slots", "Spell Slots:");
        this.castingTooltip("spell_info", "Spell Info:");
        this.castingTooltip("mana_cost", "Mana Cost:");
        this.castingTooltip("required_catalyst", "Required Catalyst:");
        this.castingTooltip("cast_time", "Cast Time:");
        this.castingTooltip("catalyst_required", "Catalyst Required");
        this.castingTooltip("do_nothing", "Nothing Happens");
        
        this.argumentInvalid("spell", "Invalid Spell ID: %s");
        this.argumentInvalid("species_type", "Invalid Species ID: %s");
        
        this.commandSuccessSingleAndAll("cast_spell", "Cast Spell %s from 1 Player [%s]", "Cast Spell %s from %s Player(s) [%s]");
        this.commandSuccessSingleAndAll("clear_spell", "Cleared Spell from Slot %s", "Cleared Spells from All Slots");
        
        this.command("list_features.spells", "There Are Currently %s Spells Loaded:");
        this.command("list_features.species", "There Are Currently %s Species Loaded:");
        this.command("list_features.class_types", "There Are Currently %s Class Types Loaded:");
        this.command("list_features.diets", "There Are Currently %s Diets Loaded:");
        this.command("welcome_message.enabled", "Enabled Welcome Message on World Join");
        this.command("welcome_message.disabled", "Disabled Welcome Message on World Join");
        
        this.key("activate_class_ability", "Activate Class Ability");
        this.key("activate_species_ability", "Activate Species Ability");
        
        this.keyCategory("incompetent_core", "Incompetent Core");
        
        this.tag(IncompItemTags.carnivoreFriendly, "Carnivore Friendly");
        this.tag(IncompItemTags.veganFriendly, "Vegan Friendly");
        this.tag(IncompItemTags.omnivoreFriendly, "Omnivore Friendly");
        this.tag(IncompItemTags.vegetarianFriendly, "Vegetarian Friendly");
        this.tag(IncompItemTags.neutralFood, "Neutral Food");
        this.tag(IncompItemTags.givesHunger, "Gives Hunger");
        
        this.spell(CastingItemUtil.emptySpell, "Empty Spell Slot");
        
        this.welcomeMessage("thanks.none", "[AUTOMATIC] Thank you for using Incompetent Core! There are currently no features loaded by active datapacks.");
        this.welcomeMessage("thanks.singular", "[AUTOMATIC] Thank you for using Incompetent Core! There is currently %s feature loaded by active datapacks.");
        this.welcomeMessage("thanks.plural", "[AUTOMATIC] Thank you for using Incompetent Core! There are currently %s features loaded by active datapacks.");
        this.welcomeMessage("spells.none", "> No Spells");
        this.welcomeMessage("spells.singular", "> 1 Spell");
        this.welcomeMessage("spells.plural", "> %s Spells");
        this.welcomeMessage("species.none", "> No Species");
        this.welcomeMessage("species.singular", "> 1 Species");
        this.welcomeMessage("species.plural", "> %s Species");
        this.welcomeMessage("class_types.none", "> No Class Types");
        this.welcomeMessage("class_types.singular", "> 1 Class Type");
        this.welcomeMessage("class_types.plural", "> %s Class Types");
        this.welcomeMessage("diets.none", "> No Diets");
        this.welcomeMessage("diets.singular", "> 1 Diet");
        this.welcomeMessage("diets.plural", "> %s Diets");
        this.welcomeMessage("enabled", "[AUTOMATIC] Welcome Message on World Join is enabled.");
        this.welcomeMessage("enabled.toggle", "To disable, use %s");
        
        this.add("ability.incompetent_core.function_charges.no_charges", "You have no charges remaining for this ability.");
        this.add("tooltip.incompetent_core.cooldown_length_seconds", " %d Cooldown");
        this.add("incompetent_core.abilities.duration_seconds", "Lasts %d");
        this.add("incompetent_core.abilities.applied_effect", "Applies: %s %s");
        this.add("incompetent_core.tooltip.inedible", "Inedible");
        this.add("incompetent_core.tooltip.cannot_use", "You do not know how to use this item");
        this.add("incompetent_core.tooltip.cannot_wear", "You cannot wear this equipment");
    }
    
    private void tab(Holder<CreativeModeTab> tabHolder) {
        this.add(tabHolder, "itemGroup");
    }
    
    private void block(Holder<Block> blockHolder) {
        this.add(blockHolder, "block");
    }
    
    private void item(Holder<Item> itemHolder) {
        this.add(itemHolder, "item");
    }
    
    private void welcomeMessage(String key, String value) {
        string("incompetent_core.welcome_message.%s".formatted(key), value);
    }
    
    private void argumentInvalid(String key, String value) {
        string("argument.%s.id.invalid".formatted(key), value);
    }
    private void command(String key, String value) {
        string("commands.incompetent_core.%s".formatted(key), value);
    }
    private void commandSuccessSingle(String key, String value) {
        string("commands.%s.success.single".formatted(key), value);
    }
    private void commandSuccessAll(String key, String value) {
        string("commands.%s.success.all".formatted(key), value);
    }
    private void commandSuccessSingleAndAll(String key, String single, String all) {
        commandSuccessSingle(key, single);
        commandSuccessAll(key, all);
    }
    private void castingTooltip(String key, String value) {
        string("item.incompetent_core.spellcasting.%s".formatted(key), value);
    }
    private void key(String key, String value) {
        string("key.incompetent_core.%s".formatted(key), value);
    }
    private void keyCategory(String key, String value) {
        string("key.categories.%s".formatted(key), value);
    }
    private void tag(TagKey<Item> tag, String value) {
        ResourceLocation tagId = tag.location();
        String tagNamespace = tagId.getNamespace().equals("forge") ? "c" : tagId.getNamespace();
        string("tag.item.%s.%s".formatted(tagNamespace, tagId.getPath().replace('/', '.')), value);
    }
    private void spell(ResourceLocation spell, String value) {
        string("spells." + spell.getNamespace() + "." + spell.getPath().replace("/", "."), value);
    }
    private void string(String key, String value) {
        super.add(key, value);
    }
    private void enchantment(Holder<Enchantment> holder) {
        this.add(holder, "enchantment");
    }
    private void effect(Holder<MobEffect> holder) {
        this.add(holder, "effect");
    }
    private void container(String containerName){
        String translated = transform(containerName);
        super.add("container.%s".formatted(containerName), translated);
    }
    
    private void add(Holder<?> holder, String type) {
        ResourceKey<?> resourceKey = holder.unwrapKey().orElseThrow(() -> new NoSuchElementException("No respective key. Check log"));
        ResourceLocation path = resourceKey.location();
        super.add(path.toLanguageKey(type), this.transform(path));
    }
    /**
     * Use to transform a ResourceLocation-form text into a spaced-form text.
     * e.g. example_transform_text -> Example Transform Text
     */
    private String transform(ResourceLocation id) {
        return this.transform(id.getPath());
    }
    
    
    /**
     * Use to transform a ResourceLocation-form text into a spaced-form text.
     * e.g. example_transform_text -> Example Transform Text
     */
    private String transform(String path) {
        int pathLength = path.length();
        StringBuilder stringBuilder = new StringBuilder(pathLength).append(Character.toUpperCase(path.charAt(0)));
        for (int i = 1; i < pathLength; i++) {
            char posChar = path.charAt(i);
            if (posChar == '_') {
                stringBuilder.append(' ');
            } else if (path.charAt(i - 1) == '_') {
                stringBuilder.append(Character.toUpperCase(posChar));
            } else stringBuilder.append(posChar);
        }
        return stringBuilder.toString();
    }
}
