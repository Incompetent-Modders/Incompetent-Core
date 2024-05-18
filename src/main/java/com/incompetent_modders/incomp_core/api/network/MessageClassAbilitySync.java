package com.incompetent_modders.incomp_core.api.network;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeProperties;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MessageClassAbilitySync(boolean applyCooldown) implements CustomPacketPayload {
    public static final Type<MessageClassAbilitySync> TYPE = new Type<>(new ResourceLocation(IncompCore.MODID, "class_ability_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageClassAbilitySync> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            MessageClassAbilitySync::applyCooldown,
            MessageClassAbilitySync::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(final MessageClassAbilitySync message, final IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ResourceLocation classType = PlayerDataCore.ClassData.getPlayerClassType(player);
            ClassTypeProperties classTypeProperties = ClassTypeListener.getClassTypeProperties(classType);
            boolean canUseAbility = PlayerDataCore.ClassData.canUseAbility(player);
            if (canUseAbility) {
                classTypeProperties.ability().apply(player.level(), player);
                PlayerDataCore.ClassData.setAbilityCooldown(player, message.applyCooldown() ? classTypeProperties.abilityCooldown() : 0);
            }
        });
    }
}
