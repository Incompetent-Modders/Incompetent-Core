package com.incompetent_modders.incomp_core.core.player.class_type;

import com.incompetent_modders.incomp_core.common.registry.ModAttachmentTypes;
import com.incompetent_modders.incomp_core.core.network.clientbound.UpdateClassTypePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityClassTypeProvider<T extends LivingEntity> implements ClassTypeProvider {

    private static final AttachmentType<ClassTypeStorage> ATTACHMENT = ModAttachmentTypes.CLASS_TYPE.get();


    final T entity;

    public EntityClassTypeProvider(T entity) {
        this.entity = entity;
    }

    @Override
    public ClassTypeStorage asStorage() {
        return this.entity.getData(ATTACHMENT);
    }

    @Override
    public void setStorage(ClassTypeStorage storage) {
        this.entity.setData(ATTACHMENT, storage);

        if (this.entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new UpdateClassTypePayload(storage));
        }
    }


}
