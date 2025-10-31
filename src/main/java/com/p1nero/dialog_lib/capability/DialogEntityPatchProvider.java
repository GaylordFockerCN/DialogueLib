package com.p1nero.dialog_lib.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DialogEntityPatchProvider implements ICapabilityProvider, NonNullSupplier<DialogEntityPatch> {

    private final DialogEntityPatch dialogEntityPatch;

    private final LazyOptional<DialogEntityPatch> optional = LazyOptional.of(this);

    public DialogEntityPatchProvider(Entity entity) {
        this.dialogEntityPatch = new DialogEntityPatch(entity);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == DialogueLibCapabilities.DIALOG_ENTITY){
            return optional.cast();
        }
        return LazyOptional.empty();
    }

//    @Override
//    public CompoundTag serializeNBT() {
//        CompoundTag tag = new CompoundTag();
//        dialogEntityPatch.saveNBTData(tag);
//        return tag;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag tag) {
//        dialogEntityPatch.loadNBTData(tag);
//    }

    @Override
    public @NotNull DialogEntityPatch get() {
        return dialogEntityPatch;
    }

}
