package com.incompetent_modders.incomp_core.api.json.spell;

import com.incompetent_modders.incomp_core.common.util.ByteBufUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs.*;

public record Catalyst(ItemStack item, boolean keepCatalyst, boolean dropLeftoverItems) {
    public static final Codec<Catalyst> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(Catalyst::item),
            Codec.BOOL.optionalFieldOf("keep_catalyst", false).forGetter(Catalyst::keepCatalyst),
            Codec.BOOL.optionalFieldOf("drop_leftover_items", false).forGetter(Catalyst::dropLeftoverItems)
    ).apply(instance, Catalyst::new));
    
    public static Catalyst EMPTY = new Catalyst(ItemStack.EMPTY, false, false);
    
    public void write(RegistryFriendlyByteBuf buf) {
        ItemStack.STREAM_CODEC.encode(buf, item);
        buf.writeBoolean(keepCatalyst);
        buf.writeBoolean(dropLeftoverItems);
    }
    
    
    
    public static Catalyst decode(RegistryFriendlyByteBuf buf) {
        var item = ItemStack.STREAM_CODEC.decode(buf);
        var keep = buf.readBoolean();
        var drop = buf.readBoolean();
        return new Catalyst(item, keep, drop);
    }
    
    public void toNetwork(FriendlyByteBuf buf) {
        ByteBufUtils.writeItem(item, buf);
        buf.writeBoolean(keepCatalyst);
        buf.writeBoolean(dropLeftoverItems);
    }
    
    public static Catalyst fromNetwork(FriendlyByteBuf buf) {
        var item = ByteBufUtils.readItem(buf);
        var keep = buf.readBoolean();
        var drop = buf.readBoolean();
        return new Catalyst(item, keep, drop);
    }
}
