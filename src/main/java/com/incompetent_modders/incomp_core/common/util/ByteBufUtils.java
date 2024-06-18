package com.incompetent_modders.incomp_core.common.util;

import net.minecraft.core.IdMap;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ByteBufUtils {
    public static <T> void writeId(IdMap<T> idMap, T t, FriendlyByteBuf buf) {
        int id = idMap.getId(t);
        if (id == -1) {
            throw new IllegalArgumentException("Can't find id for '" + t + "' in map " + idMap);
        } else {
            buf.writeVarInt(id);
        }
    }
    @Nullable
    public static <T> T readById(IdMap<T> idMap, FriendlyByteBuf buf) {
        int var2 = buf.readVarInt();
        return (T)idMap.byId(var2);
    }
    
    public static FriendlyByteBuf writeItem(ItemStack itemStack, FriendlyByteBuf buf) {
        if (itemStack.isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            Item item = itemStack.getItem();
            writeId(BuiltInRegistries.ITEM, item, buf);
            buf.writeByte(itemStack.getCount());
            DataComponentMap componentMap = null;
            if (item.getDamage(itemStack) > 0) {
                componentMap = itemStack.getComponents();
            }
        }
        return buf;
    }
    
    public static ItemStack readItem(FriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            Item var1 = readById(BuiltInRegistries.ITEM, buf);
            byte var2 = buf.readByte();
            return new ItemStack(var1, var2);
        }
    }
}
