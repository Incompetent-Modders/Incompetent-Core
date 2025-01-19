package com.incompetent_modders.incomp_core.core.player.mana;

import com.incompetent_modders.incomp_core.common.registry.ModAttachmentTypes;
import com.incompetent_modders.incomp_core.core.network.UpdateManaPayload;
import com.incompetent_modders.incomp_core.core.player.species_type.SpeciesTypeProvider;
import com.incompetent_modders.incomp_core.core.player.species_type.SpeciesTypeStorage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityManaProvider<T extends LivingEntity> implements ManaProvider {

    private static final AttachmentType<ManaStorage> ATTACHMENT = ModAttachmentTypes.MANA.get();


    final T entity;

    public EntityManaProvider(T entity) {
        this.entity = entity;
    }

    @Override
    public ManaStorage asStorage() {
        return this.entity.getData(ATTACHMENT);
    }

    @Override
    public void setStorage(ManaStorage storage) {
        this.entity.setData(ATTACHMENT, storage);

        if (this.entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new UpdateManaPayload(storage));
        }
    }
}
