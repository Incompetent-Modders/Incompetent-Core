package com.incompetent_modders.incomp_core.common.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class IncompetentCodecs {
    public static final Codec<Map<Integer, ResourceLocation>> MAP_OF_INT_RESOURCE_LOCATION = Codec.unboundedMap(Codec.INT, ResourceLocation.CODEC);
    
    public static final StreamCodec<ByteBuf, Map<Integer, ResourceLocation>> MAP_OF_INT_RESOURCE_LOCATION_BYTE = new StreamCodec<>() {
        @Override
        public void encode(@NotNull ByteBuf byteBuf, Map<Integer, ResourceLocation> integerResourceLocationMap) {
            byteBuf.writeInt(integerResourceLocationMap.size());
            for (Map.Entry<Integer, ResourceLocation> entry : integerResourceLocationMap.entrySet()) {
                byteBuf.writeInt(entry.getKey());
                Utf8String.write(byteBuf, entry.getValue().toString(), 32767);
            }
        }
        
        @Override
        public @NotNull Map<Integer, ResourceLocation> decode(ByteBuf byteBuf) {
            int size = byteBuf.readInt();
            Map<Integer, ResourceLocation> result = new HashMap<>();
            for (int i = 0; i < size; i++) {
                int key = byteBuf.readInt();
                String locationString = Utf8String.read(byteBuf, 32767);
                ResourceLocation resourceLocation = ResourceLocation.parse(locationString);
                result.put(key, resourceLocation);
            }
            return result;
        }
    };
}
