package com.incompetent_modders.incomp_core.core.player.mana;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record ManaStorage(ManaData data, boolean showInTooltip) implements TooltipProvider {
    public static final ManaStorage EMPTY = new ManaStorage(ManaData.EMPTY, true);

    public static final Codec<ManaStorage> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ManaData.CODEC.fieldOf("data").forGetter(ManaStorage::data),
            Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(ManaStorage::showInTooltip)
    ).apply(instance, ManaStorage::new));

    public static final Codec<ManaStorage> CODEC = Codec.withAlternative(FULL_CODEC,
            RecordCodecBuilder.create(instance -> instance.group(
                    ManaData.CODEC.fieldOf("data").forGetter(ManaStorage::data)
            ).apply(instance, (data) -> new ManaStorage(data, true)))
    );

    public static final StreamCodec<FriendlyByteBuf, ManaStorage> STREAM_CODEC = StreamCodec.composite(
            ManaData.STREAM_CODEC,
            ManaStorage::data,
            ByteBufCodecs.BOOL,
            ManaStorage::showInTooltip,
            ManaStorage::new
    );

    public static Codec<ManaStorage> codec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("amount").forGetter(storage -> storage.data.amount()),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("limit").forGetter(storage -> storage.data.limit())
        ).apply(instance, (amount, limit) -> new ManaStorage(new ManaData(amount, limit), true)));
    }

    public static ManaStorage createDefault() {
        return new ManaStorage(ManaData.of(0, 0), true);
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.literal(": " + this.data.amount() + "/" + this.data.limit()).withStyle(ChatFormatting.GRAY));
    }
}
