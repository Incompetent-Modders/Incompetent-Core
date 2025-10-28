package com.incompetent_modders.incomp_core.api.species.diet;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.data.IncompItemTags;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.core.def.attributes.species.DietAttribute;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Predicate;

import static com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper.getSpeciesType;

@EventBusSubscriber
public record Diet(NonNullList<Ingredient> ableToConsume) {

    public static final Codec<Diet> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("able_to_consume").flatXmap((ingredientList) -> {
                Ingredient[] aingredient = ingredientList.toArray(Ingredient[]::new);
                if (aingredient.length == 0) {
                    return DataResult.error(() -> "No inputs for diet");
                } else {
                    return DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                }
            }, DataResult::success).forGetter(Diet::ableToConsume)
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
            }, DataResult::success).forGetter(Diet::ableToConsume)
    ).apply(instance, Diet::new));

    public Diet(NonNullList<Ingredient> ableToConsume) {
        this.ableToConsume = ableToConsume;
    }

    static {
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.Keys.DIET);
    }

    @SubscribeEvent
    public static void onItemUse(LivingEntityUseItemEvent.Start event) {
        if (!event.getItem().has(DataComponents.FOOD)) return; // Only care about food
        DietAttribute dietAttribute = getSpeciesType(event.getEntity()).get(ModSpeciesAttributes.DIET.get());
        if (dietAttribute != null) {
            Holder<Diet> dietHolder = dietAttribute.diet;
            if(!dietHolder.value().isEdible(event.getItem())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (!event.getItemStack().has(DataComponents.FOOD)) return; // Only care about food
        DietAttribute dietAttribute = getSpeciesType(event.getEntity()).get(ModSpeciesAttributes.DIET.get());
        if (dietAttribute != null) {
            Holder<Diet> dietHolder = dietAttribute.diet;
            if (!dietHolder.value().isEdible(event.getItemStack())) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
            }
        }
    }

    public boolean isEdible(ItemStack stack) {
        Predicate<ItemStack> predicate = (itemStack) ->
                ableToConsume().stream().anyMatch((ingredient) -> ingredient.test(itemStack));
        return predicate.test(stack);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final NonNullList<Ingredient> ableToConsume = NonNullList.create();
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

        public Builder inheritFrom(Builder other) {
            this.ableToConsume.addAll(other.ableToConsume);
            this.ignoreNeutralFood = other.ignoreNeutralFood;
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

        public Builder ignoreNeutralFood() {
            this.ignoreNeutralFood = true;
            return this;
        }

        public Diet build() {
            if (!ignoreNeutralFood)
                ableToConsume.add(Ingredient.of(IncompItemTags.neutralFood));
            return new Diet(ableToConsume);
        }
    }

    public static Component getDisplayName(ResourceLocation diet) {
        return Component.translatable("diet." + diet.getNamespace() + "." + diet.getPath().replace("/", "."));
    }
}
