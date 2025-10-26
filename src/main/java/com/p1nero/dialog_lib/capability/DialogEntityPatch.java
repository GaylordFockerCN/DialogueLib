package com.p1nero.dialog_lib.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class DialogEntityPatch {

    private ServerPlayer conservingPlayer;

    public void setConservingPlayer(@Nullable ServerPlayer conservingPlayer) {
        this.conservingPlayer = conservingPlayer;
    }

    public @Nullable ServerPlayer getCurrentTalkingPlayer() {
        return conservingPlayer;
    }


    public CompoundTag saveNBTData(CompoundTag tag) {
        return tag;
    }

    public void loadNBTData(CompoundTag tag) {

    }

}
