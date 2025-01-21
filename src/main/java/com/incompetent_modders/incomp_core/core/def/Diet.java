package com.incompetent_modders.incomp_core.core.def;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.data.IncompItemTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

public record Diet(NonNullList<Ingredient> ableToConsume, boolean ignoreHunger) {
    public static final Codec<Diet> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("able_to_consume").flatXmap((ingredientList) -> {
                Ingredient[] aingredient = ingredientList.toArray(Ingredient[]::new);
                if (aingredient.length == 0) {
                    return DataResult.error(() -> "No inputs for diet");
                } else {
                    return DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                }
            }, DataResult::success).forGetter(Diet::ableToConsume),
            Codec.BOOL.optionalFieldOf("ignore_hunger", false).forGetter(Diet::ignoreHunger)
    ).apply(instance, Diet::new));

    public static final Codec<Holder<Diet>> CODEC = RegistryFixedCodec.create(ModRegistries.Keys.DIET);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Diet>> STREAM_CODEC;

    public static final Codec<Diet> NETWORK_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("able_to_consume").flatXmap((ingredientList) -> {
                Ingredient[] aingredient = ingredientList.toArray(Ingredient[]::new);
                if (aingredient.length == 0) {
                    return DataResult.error(() -> "No inputs for diet");
                } else {
                    return DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                }
            }, DataResult::success).forGetter(Diet::ableToConsume),
            Codec.BOOL.optionalFieldOf("ignore_hunger", false).forGetter(Diet::ignoreHunger)
    ).apply(instance, Diet::new));

    public Diet(NonNullList<Ingredient> ableToConsume, boolean ignoreHunger) {
        this.ableToConsume = ableToConsume;
        this.ignoreHunger = ignoreHunger;
    }

    static {
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.Keys.DIET);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final NonNullList<Ingredient> ableToConsume = NonNullList.create();
        private boolean ignoreHunger = false;
        private boolean ignoreNeutralFood = false;

        public Builder() {
        }

        public Builder addConsumable(Ingredient ingredient) {
            ableToConsume.add(ingredient);
            return this;
        }

        public Builder addConsumable(Item item) {
            ableToConsume.add(Ingredient.of(item));
            return this;
        }

        public Builder addConsumable(Item... items) {
            for (Item item : items) {
                ableToConsume.add(Ingredient.of(item));
            }
            return this;
        }

        public Builder addConsumable(TagKey<Item> tag) {
            ableToConsume.add(Ingredient.of(tag));
            return this;
        }

        @SafeVarargs
        public final Builder addConsumable(TagKey<Item>... tags) {
            for (TagKey<Item> tag : tags) {
                ableToConsume.add(Ingredient.of(tag));
            }
            return this;
        }

        public Builder ignoreHunger() {
            this.ignoreHunger = true;
            return this;
        }

        public Builder ignoreNeutralFood() {
            this.ignoreNeutralFood = true;
            return this;
        }

        public Diet build() {
            if (!ignoreNeutralFood)
                ableToConsume.add(Ingredient.of(IncompItemTags.neutralFood));
            return new Diet(ableToConsume, ignoreHunger);
        }
    }

    public static Component getDisplayName(ResourceLocation diet) {
        return Component.translatable("diet." + diet.getNamespace() + "." + diet.getPath().replace("/", "."));
    }
}
