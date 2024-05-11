package com.incompetent_modders.incomp_core.api.player_data.species;

import com.google.common.collect.Lists;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.json.species.DietType;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesAttributesListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviour;
import com.incompetent_modders.incomp_core.registry.ModSpeciesTypes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TickEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class SpeciesType {
    public static final Codec<Holder<SpeciesType>> DIRECT_CODEC = ModRegistries.SPECIES_TYPE
            .holderByNameCodec()
            .validate(
                    DataResult::success
            );
    @Nullable
    private String descriptionId;
    
    public SpeciesType() {
    }
    public boolean is(TagKey<SpeciesType> speciesTypeTagKey) {
        return this.builtInRegistryHolder.is(speciesTypeTagKey);
    }
    
    public final SpeciesType getSpeciesType(ResourceLocation rl) {
        if (rl.equals(this.getSpeciesTypeIdentifier())) {
            return this;
        } else {
            IncompCore.LOGGER.error("Species Type: " + rl + " does not exist!");
            return null;
        }
    }
    
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("species_type", ModRegistries.SPECIES_TYPE.getKey(this));
        }
        
        return this.descriptionId;
    }
    
    public Component getDisplayName() {
        return Component.translatable(this.getOrCreateDescriptionId());
    }
    
    public ResourceLocation getSpeciesTypeIdentifier() {
        return ModRegistries.SPECIES_TYPE.getKey(this);
    }
    boolean hasProperties() {
        return SpeciesListener.getSpeciesTypeProperties(this) != null;
    }
    
    public SpeciesBehaviour speciesBehaviour() {
        return SpeciesListener.getSpeciesTypeProperties(this).behaviour();
    }
    public boolean isInvertedHealAndHarm() {
        return SpeciesListener.getSpeciesTypeProperties(this).invertHealAndHarm();
    }
    public boolean keepOnDeath() {
        return SpeciesListener.getSpeciesTypeProperties(this).keepOnDeath();
    }
    public DietType dietType() {
        return SpeciesListener.getSpeciesTypeProperties(this).dietType();
    }
    public double maxHealth() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).maxHealth();
    }
    public double attackDamage() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).attackDamage();
    }
    public double attackKnockback() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).attackKnockback();
    }
    public double movementSpeed() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).moveSpeed();
    }
    public double armor() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).armour();
    }
    public double luck() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).luck();
    }
    public double blockInteractionRange() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).blockInteractionRange();
    }
    public double entityInteractionRange() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).entityInteractionRange();
    }
    public double gravity() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).gravity();
    }
    public double jumpStrength() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).jumpStrength();
    }
    public double knockbackResistance() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).knockbackResistance();
    }
    public double safeFallDistance() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).safeFallDistance();
    }
    public double scale() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).scale();
    }
    public double stepHeight() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).stepHeight();
    }
    public double armourToughness() {
        return SpeciesAttributesListener.getSpeciesTypeAttributes(this).armourToughness();
    }
    
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        SpeciesType speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
        if (speciesType == null) {
            return;
        }
        AttributeInstance maxHealthInst = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attackDamageInst = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance attackKnockbackInst = player.getAttribute(Attributes.ATTACK_KNOCKBACK);
        AttributeInstance movementSpeedInst = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance armorInst = player.getAttribute(Attributes.ARMOR);
        AttributeInstance luckInst = player.getAttribute(Attributes.LUCK);
        AttributeInstance blockInteractionRangeInst = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityInteractionRangeInst = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
        AttributeInstance jumpStrength = player.getAttribute(Attributes.JUMP_STRENGTH);
        AttributeInstance knockbackResistance = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        AttributeInstance safeFallDistance = player.getAttribute(Attributes.SAFE_FALL_DISTANCE);
        AttributeInstance scale = player.getAttribute(Attributes.SCALE);
        AttributeInstance stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);
        AttributeInstance armourToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (maxHealthInst == null || attackDamageInst == null || attackKnockbackInst == null || movementSpeedInst == null || armorInst == null || luckInst == null || blockInteractionRangeInst == null || entityInteractionRangeInst == null || gravity == null || jumpStrength == null || knockbackResistance == null || safeFallDistance == null || scale == null || stepHeight == null || armourToughness == null) {
            return;
        }
        maxHealthInst.setBaseValue(speciesType.maxHealth());
        attackDamageInst.setBaseValue(speciesType.attackDamage());
        attackKnockbackInst.setBaseValue(speciesType.attackKnockback());
        movementSpeedInst.setBaseValue(speciesType.movementSpeed());
        armorInst.setBaseValue(speciesType.armor());
        luckInst.setBaseValue(speciesType.luck());
        blockInteractionRangeInst.setBaseValue(speciesType.blockInteractionRange());
        entityInteractionRangeInst.setBaseValue(speciesType.entityInteractionRange());
        gravity.setBaseValue(speciesType.gravity());
        jumpStrength.setBaseValue(speciesType.jumpStrength());
        knockbackResistance.setBaseValue(speciesType.knockbackResistance());
        safeFallDistance.setBaseValue(speciesType.safeFallDistance());
        scale.setBaseValue(speciesType.scale());
        stepHeight.setBaseValue(speciesType.stepHeight());
        armourToughness.setBaseValue(speciesType.armourToughness());
        SpeciesBehaviour behaviour = speciesType.speciesBehaviour();
        behaviour.apply(player.level(), player);
    }
    private final Holder.Reference<SpeciesType> builtInRegistryHolder = ModRegistries.SPECIES_TYPE.createIntrusiveHolder(this);
    public Holder.Reference<SpeciesType> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }
    
    public static record TagValue(TagKey<SpeciesType> tag) implements Value {
        static final com.mojang.serialization.MapCodec<TagValue> MAP_CODEC = RecordCodecBuilder.mapCodec(
                p_301118_ -> p_301118_.group(TagKey.codec(ModRegistries.SPECIES_TYPE.key()).fieldOf("tag").forGetter(p_301154_ -> p_301154_.tag))
                        .apply(p_301118_, TagValue::new)
        );
        static final Codec<TagValue> CODEC = MAP_CODEC.codec();
        
        @Override
        public boolean equals(Object p_301162_) {
            return p_301162_ instanceof TagValue species$tagValue && species$tagValue.tag.location().equals(this.tag.location());
        }
        @Override
        public Collection<SpeciesType> getSpecies() {
            List<SpeciesType> list = Lists.newArrayList();
            
            for (Holder<SpeciesType> holder : ModRegistries.SPECIES_TYPE.getTagOrEmpty(this.tag)) {
                list.add(holder.value());
            }
            
            if (list.isEmpty()) {
                list.add(ModSpeciesTypes.HUMAN.get());
            }
            return list;
        }
    }
    public static record SpeciesValue(Holder<SpeciesType> species) implements Value {
        static final com.mojang.serialization.MapCodec<SpeciesValue> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(ModRegistries.SPECIES_TYPE.holderByNameCodec().fieldOf("id").forGetter(value -> value.species))
                        .apply(instance, SpeciesValue::new)
        );
        static final Codec<SpeciesValue> CODEC = MAP_CODEC.codec();
        
        @Override
        public Collection<SpeciesType> getSpecies() {
            return Collections.singleton(this.species.value());
        }
    }
    public interface Value {
        MapCodec<Value> MAP_CODEC = net.neoforged.neoforge.common.util.NeoForgeExtraCodecs.xor(SpeciesValue.MAP_CODEC, TagValue.MAP_CODEC)
                .xmap(p_300956_ -> p_300956_.map(p_300932_ -> p_300932_, p_301313_ -> p_301313_), s -> {
                    if (s instanceof TagValue species$tagValue) {
                        return Either.right(species$tagValue);
                    } else if (s instanceof SpeciesValue ingredient$itemvalue) {
                        return Either.left(ingredient$itemvalue);
                    } else {
                        throw new UnsupportedOperationException("This is neither a species value nor a tag value.");
                    }
                });
        Codec<Value> CODEC = MAP_CODEC.codec();
        
        Collection<SpeciesType> getSpecies();
    }
    public static Value valueOf(Holder<SpeciesType> speciesType) {
        return new SpeciesValue(speciesType);
    }
    public static Value valueOf(TagKey<SpeciesType> tag) {
        return new TagValue(tag);
    }
}
