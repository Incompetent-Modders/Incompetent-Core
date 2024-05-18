package com.incompetent_modders.incomp_core.data;

import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.NoSuchElementException;

public abstract class IncompLangProvider extends LanguageProvider {
    public IncompLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
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
    private void classType(Holder<ClassType> holder) {
        this.add(holder, "class_type");
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
