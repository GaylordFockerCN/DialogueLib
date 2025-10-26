package com.p1nero.dialog_lib.capability;

import com.p1nero.dialog_lib.DialogueLib;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.clientbound.SyncDialogPlayerDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class DialogPlayerData <E extends Entity> {

    private Entity currentTalkingEntity;

    public void setCurrentTalkingEntity(@Nullable Entity currentTalkingEntity) {
        this.currentTalkingEntity = currentTalkingEntity;
    }

    public @Nullable Entity getCurrentTalkingEntity() {
        return currentTalkingEntity;
    }


    public CompoundTag saveNBTData(CompoundTag tag) {
        return tag;
    }

    public void loadNBTData(CompoundTag tag) {

    }

    public void copyFrom(DialogPlayerData old) {
    }

    public void syncToClient(ServerPlayer serverPlayer) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new SyncDialogPlayerDataPacket(saveNBTData(new CompoundTag())), serverPlayer);
    }

    public void tick(Player player) {
        if(player instanceof ServerPlayer serverPlayer){
            handleTalking(serverPlayer);
        }
    }

    public void handleTalking(ServerPlayer serverPlayer) {
        DialogueLib.runIfExtensionExist(serverPlayer, currentTalkingEntity, (iEntityDialogueExtension -> {
            if(currentTalkingEntity.distanceTo(serverPlayer) > iEntityDialogueExtension.maxTalkDistance()) {
                currentTalkingEntity = null;
                return;
            }
            if(iEntityDialogueExtension.shouldLookAtPlayer(serverPlayer, currentTalkingEntity)) {
                if(currentTalkingEntity instanceof Mob mob) {
                    mob.getLookControl().setLookAt(serverPlayer);
                    mob.getNavigation().stop();
                }
            }
            iEntityDialogueExtension.onTalkingTick(serverPlayer, currentTalkingEntity);
        }));
    }

}
