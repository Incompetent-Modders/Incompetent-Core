package com.incompetent_modders.incomp_core.core.player.species_type;

import com.incompetent_modders.incomp_core.common.registry.ModAttachmentTypes;
import com.incompetent_modders.incomp_core.core.network.clientbound.UpdateSpeciesTypePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntitySpeciesTypeProvider<T extends LivingEntity> implements SpeciesTypeProvider {

    private static final AttachmentType<SpeciesTypeStorage> ATTACHMENT = ModAttachmentTypes.SPECIES_TYPE.get();


    final T entity;

    public EntitySpeciesTypeProvider(T entity) {
        this.entity = entity;
    }

    @Override
    public SpeciesTypeStorage asStorage() {
        return this.entity.getData(ATTACHMENT);
    }

    @Override
    public void setStorage(SpeciesTypeStorage storage) {
        this.entity.setData(ATTACHMENT, storage);

        if (this.entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new UpdateSpeciesTypePayload(storage));
        }
    }


}
