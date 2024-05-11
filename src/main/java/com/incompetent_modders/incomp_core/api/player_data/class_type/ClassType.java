package com.incompetent_modders.incomp_core.api.player_data.class_type;

import com.google.common.collect.Lists;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.ClassAbility;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffect;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.registry.ModSpeciesTypes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TickEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The Class Type of a player.
 * <p>
 * The class type determines the abilities and stats of a player.
 * <p>
 * Class types are registered using the CLASS_TYPE DeferredRegister in ModRegistries.
 * @see ModRegistries#CLASS_TYPE
 * @see ModClassTypes
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class ClassType {
    private final Holder.Reference<ClassType> builtInRegistryHolder = ModRegistries.CLASS_TYPE.createIntrusiveHolder(this);
    public Holder.Reference<ClassType> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }
    
    public static final Codec<Holder<ClassType>> DIRECT_CODEC = ModRegistries.CLASS_TYPE
            .holderByNameCodec()
            .validate(
                    holder -> holder.is(ModClassTypes.NONE.get().builtInRegistryHolder())
                            ? DataResult.error(() -> "Cannot serialize default class type.")
                            : DataResult.success(holder)
            );
    @Nullable
    private String descriptionId;
    
    public ClassType() {
    }
    
    /**
     * Returns the maximum mana of the class type.
     * <p>
     * If the class type cannot cast spells, the maximum mana is 0.
     */
    public int getMaxMana() {
        return canCastSpells() ? ClassTypeListener.getClassTypeProperties(this).maxMana() : 0;
    }
    
    /**
     * Returns true if the class type can cast spells.
     * <p>
     * A class type that can cast spells has a mana bar and can cast spells.
     */
    public boolean canCastSpells() {
        return ClassTypeListener.getClassTypeProperties(this).canCastSpells();
    }
    
    /**
     * Returns true if the class type is pacifist.
     * <p>
     * A pacifist class type has decreased damage.
     */
    public boolean isPacifist() {
        return ClassTypeListener.getClassTypeProperties(this).pacifist();
    }
    
    /**
     * Returns the frequency at which the passive effect of the class type is applied.
     */
    public int getPassiveEffectTickFrequency() {
        return ClassTypeListener.getClassTypeProperties(this).passiveEffectTickFrequency();
    }
    
    public int abilityCooldown() {
        return ClassTypeListener.getClassTypeProperties(this).abilityCooldown();
    }
    /**
     * Returns the class type with the specified identifier.
     * <p>
     * If no class type with the specified identifier exists, an error message is logged and null is returned.
     * @param rl The identifier of the class type.
     * @return The class type with the specified identifier.
     */
    public final ClassType getClassType(ResourceLocation rl) {
        if (rl.equals(this.getClassTypeIdentifier())) {
            return this;
        } else {
            IncompCore.LOGGER.error("Class Type: " + rl + " does not exist!");
            return null;
        }
    }
    
    /**
     * Returns the description identifier of the class type.
     * <p>
     * Used in the translation file to display the name of the class type.
     */
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("class_type", ModRegistries.CLASS_TYPE.getKey(this));
        }
        
        return this.descriptionId;
    }
    
    /**
     * Returns the display name of the class type.
     */
    public Component getDisplayName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }
    
    /**
     * Returns the identifier of the class type.
     */
    public ResourceLocation getClassTypeIdentifier() {
        return ModRegistries.CLASS_TYPE.getKey(this);
    }
    
    
    /**
     * The passive effect of the class type.
     * <p>
     * This method is called every tick but only applies the effect at the frequency specified by the passiveEffectTickFrequency field.
     * <p>
     * If left empty, the class type will have no passive effect.
     */
    public ClassPassiveEffect classPassiveEffect() {
        return ClassTypeListener.getClassTypeProperties(this).passiveEffect();
    }
    
    /**
     * The ability of the class type.
     * <p>
     * This method is called when the player presses the ability key.
     * <p>
     * If left empty, the class type will have no ability.
     */
    public ClassAbility classAbility() {
        return ClassTypeListener.getClassTypeProperties(this).ability();
    }
    
    /**
     * Returns true if the class type can regenerate mana.
     * <p>
     * If left empty, the class type will be able to regenerate mana.
     * <p>
     * <b>Useful for classes that need to meet certain conditions to regenerate mana.</b>
     */
    public boolean canRegenerateMana(ServerPlayer player, Level level) {
        return ClassTypeListener.getClassTypeProperties(this).manaRegenCondition().apply(level, player);
    }
    
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) {
            return;
        }
        if (PlayerDataCore.ClassData.getPlayerClassType(player) == null) {
            PlayerDataCore.ClassData.setPlayerClassType(player, ModClassTypes.NONE.get());
            return;
        }
        if (PlayerDataCore.ClassData.getPlayerClassType(player).getPassiveEffectTickFrequency() == 0) {
            return;
        }
        // Only call the method if the tickFrequency is less than or equal to 0 and then reset the tickFrequency to the passiveEffectTickFrequency
        if (player.tickCount % PlayerDataCore.ClassData.getPlayerClassType(player).getPassiveEffectTickFrequency() == 0) {
            PlayerDataCore.ClassData.getPlayerClassType(player).classPassiveEffect().apply(level, (ServerPlayer) player);
        }
    }
    
    public boolean useClassSpecificTexture() {
        return ClassTypeListener.getClassTypeProperties(this).useClassSpecificTexture();
    }
    public ResourceLocation getClassSpecificTexture(String path, String name) {
        return new ResourceLocation(this.getClassTypeIdentifier().getNamespace(), "textures/" + path + "/" + this.getClassTypeIdentifier().getPath() + "/" + name);
    }
    public ResourceLocation getSpellOverlayTexture(String spriteName) {
        if (!useClassSpecificTexture())
            return new ResourceLocation(IncompCore.MODID, "spell_list/" + spriteName);
        return new ResourceLocation(this.getClassTypeIdentifier().getNamespace(), "spell_list/" + this.getClassTypeIdentifier().getPath() + "/" + spriteName);
    }
    public ResourceLocation getManaOverlayTexture(String spriteName) {
        if (!useClassSpecificTexture())
            return new ResourceLocation(IncompCore.MODID, "mana_bar/" + spriteName);
        return new ResourceLocation(this.getClassTypeIdentifier().getNamespace(), "mana_bar/" + this.getClassTypeIdentifier().getPath() + "/" + spriteName);
    }
    
    
    
    
    public static record TagValue(TagKey<ClassType> tag) implements Value {
        static final com.mojang.serialization.MapCodec<TagValue> MAP_CODEC = RecordCodecBuilder.mapCodec(
                p_301118_ -> p_301118_.group(TagKey.codec(ModRegistries.CLASS_TYPE.key()).fieldOf("tag").forGetter(p_301154_ -> p_301154_.tag))
                        .apply(p_301118_, TagValue::new)
        );
        static final Codec<TagValue> CODEC = MAP_CODEC.codec();
        
        @Override
        public boolean equals(Object p_301162_) {
            return p_301162_ instanceof TagValue class$tagValue ? class$tagValue.tag.location().equals(this.tag.location()) : false;
        }
        
        @Override
        public Collection<ClassType> getClassType() {
            List<ClassType> list = Lists.newArrayList();
            
            for (Holder<ClassType> holder : ModRegistries.CLASS_TYPE.getTagOrEmpty(this.tag)) {
                list.add(holder.value());
            }
            
            if (list.isEmpty()) {
                list.add(ModClassTypes.NONE.get());
            }
            return list;
        }
    }
    public static record ClassValue(Holder<ClassType> classType) implements Value {
        static final com.mojang.serialization.MapCodec<ClassValue> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(ModRegistries.CLASS_TYPE.holderByNameCodec().fieldOf("id").forGetter(value -> value.classType))
                        .apply(instance, ClassValue::new)
        );
        static final Codec<ClassValue> CODEC = MAP_CODEC.codec();
        
        @Override
        public Collection<ClassType> getClassType() {
            return Collections.singleton(this.classType.value());
        }
    }
    public interface Value {
        MapCodec<Value> MAP_CODEC = net.neoforged.neoforge.common.util.NeoForgeExtraCodecs.xor(ClassValue.MAP_CODEC, TagValue.MAP_CODEC)
                .xmap(p_300956_ -> p_300956_.map(p_300932_ -> p_300932_, p_301313_ -> p_301313_), s -> {
                    if (s instanceof TagValue class$tagValue) {
                        return Either.right(class$tagValue);
                    } else if (s instanceof ClassValue class$classValue) {
                        return Either.left(class$classValue);
                    } else {
                        throw new UnsupportedOperationException("This is neither a class value nor a tag value.");
                    }
                });
        Codec<Value> CODEC = MAP_CODEC.codec();
        
        Collection<ClassType> getClassType();
    }
    
    
    public static Value valueOf(Holder<ClassType> classType) {
        return new ClassValue(classType);
    }
    public static Value valueOf(TagKey<ClassType> tag) {
        return new TagValue(tag);
    }
}
