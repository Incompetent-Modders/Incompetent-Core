package com.incompetent_modders.incomp_core.api.mana;

import com.incompetent_modders.incomp_core.registry.ModCapabilities;
import com.incompetent_modders.incomp_core.IncompCore;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManaCapAttacher {
    private static class ManaCapProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        
        public static final ResourceLocation IDENTIFIER = new ResourceLocation(IncompCore.MODID, "mana");
        
        private final IManaCap backend = new ManaCap(null);
        private final LazyOptional<IManaCap> optionalData = LazyOptional.of(() -> backend);
        
        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return ModCapabilities.MANA_CAPABILITY.orEmpty(cap, this.optionalData);
        }
        
        void invalidate() {
            this.optionalData.invalidate();
        }
        
        @Override
        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }
        
        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.backend.deserializeNBT(nbt);
        }
    }
    
    public static void attach(final AttachCapabilitiesEvent<Entity> event) {
        final ManaCapProvider provider = new ManaCapProvider();
        event.addCapability(ManaCapProvider.IDENTIFIER, provider);
    }
}
