package com.incompetent_modders.incomp_core.api.network.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesProperties;
import com.incompetent_modders.incomp_core.api.player.SpeciesData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MessageSpeciesAbilitySync(boolean applyCooldown) implements CustomPacketPayload {
    public static final Type<MessageSpeciesAbilitySync> TYPE = new Type<>(new ResourceLocation(IncompCore.MODID, "species_ability_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSpeciesAbilitySync> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            MessageSpeciesAbilitySync::applyCooldown,
            MessageSpeciesAbilitySync::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(final MessageSpeciesAbilitySync message, final IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ResourceLocation species = SpeciesData.Get.playerSpecies(player);
            SpeciesProperties speciesProperties = SpeciesListener.getSpeciesTypeProperties(species);
            boolean canUseAbility = SpeciesData.Util.canUseAbility(player);
            if (canUseAbility) {
                speciesProperties.ability().apply(player.level(), player);
                SpeciesData.Set.abilityCooldown(player, message.applyCooldown() ? speciesProperties.abilityCooldown() : 0);
            }
        });
    }
}
