package com.incompetent_modders.incomp_core.core.def.params;

import com.incompetent_modders.incomp_core.common.util.ByteBufUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record CatalystCondition(ItemStack item, boolean keepCatalyst, boolean dropLeftoverItems) {
    public static final Codec<CatalystCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(CatalystCondition::item),
            Codec.BOOL.optionalFieldOf("keep_catalyst", false).forGetter(CatalystCondition::keepCatalyst),
            Codec.BOOL.optionalFieldOf("drop_leftover_items", false).forGetter(CatalystCondition::dropLeftoverItems)
    ).apply(instance, CatalystCondition::new));
    
    public static CatalystCondition EMPTY = new CatalystCondition(ItemStack.EMPTY, false, false);
    
    public void write(RegistryFriendlyByteBuf buf) {
        ItemStack.STREAM_CODEC.encode(buf, item);
        buf.writeBoolean(keepCatalyst);
        buf.writeBoolean(dropLeftoverItems);
    }
    
    
    
    public static CatalystCondition decode(RegistryFriendlyByteBuf buf) {
        var item = ItemStack.STREAM_CODEC.decode(buf);
        var keep = buf.readBoolean();
        var drop = buf.readBoolean();
        return new CatalystCondition(item, keep, drop);
    }
    
    public void toNetwork(FriendlyByteBuf buf) {
        ByteBufUtils.writeItem(item, buf);
        buf.writeBoolean(keepCatalyst);
        buf.writeBoolean(dropLeftoverItems);
    }
    
    public static CatalystCondition fromNetwork(FriendlyByteBuf buf) {
        var item = ByteBufUtils.readItem(buf);
        var keep = buf.readBoolean();
        var drop = buf.readBoolean();
        return new CatalystCondition(item, keep, drop);
    }
}
