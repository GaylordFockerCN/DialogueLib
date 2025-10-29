package com.p1nero.dialog_lib.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DialogEntityPatch {

    @Nullable
    private Player conservingPlayer;
    private UUID conservingPlayerUUID;
    private Entity original;

    public DialogEntityPatch(Entity entity) {
        original = entity;
    }

    public Entity getOriginal() {
        return original;
    }

    public void setConservingPlayer(@Nullable Player conservingPlayer) {
        this.conservingPlayer = conservingPlayer;
        if (this.conservingPlayer != null) {
            conservingPlayerUUID = conservingPlayer.getUUID();
        } else {
            conservingPlayerUUID = null;
        }
    }

    public @Nullable Player getCurrentTalkingPlayer() {
        return conservingPlayer;
    }


    public CompoundTag saveNBTData(CompoundTag tag) {
        if (conservingPlayerUUID != null) {
            tag.putUUID("conservingPlayerUUID", conservingPlayerUUID);
        }
        return tag;
    }

    public void loadNBTData(CompoundTag tag) {
        if (tag.contains("conservingPlayerUUID")) {
            conservingPlayerUUID = tag.getUUID("conservingPlayerUUID");
            conservingPlayer = original.level().getPlayerByUUID(conservingPlayerUUID);
        } else {
            conservingPlayer = null;
            conservingPlayerUUID = null;
        }
    }

}
