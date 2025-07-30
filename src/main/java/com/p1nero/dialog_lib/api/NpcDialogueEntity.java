package com.p1nero.dialog_lib.api;

import com.p1nero.dialog_lib.network.PacketHandler;
import com.p1nero.dialog_lib.network.PacketRelay;
import com.p1nero.dialog_lib.network.packet.clientbound.NPCDialoguePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface NpcDialogueEntity {
    @OnlyIn(Dist.CLIENT)
    void openDialogueScreen(CompoundTag senderData);

    /**
     * handle the callback. Don't forget to set Conserving player null after talk.
     */
    void handleNpcInteraction(ServerPlayer player, int interactionID);

    void setConversingPlayer(@Nullable Player player);

    /**
     * @param data the server data that use in {@link NpcDialogueEntity#openDialogueScreen(CompoundTag)}
     * @return true if there is no conserving player
     */
    default boolean sendDialogTo(ServerPlayer serverPlayer, CompoundTag data) {
        if(this.getConversingPlayer() == null) {
            PacketRelay.sendToPlayer(PacketHandler.INSTANCE, new NPCDialoguePacket(((LivingEntity) this).getId(), data), serverPlayer);
            this.setConversingPlayer(serverPlayer);
            return true;
        }
        return false;
    }

    /**
     * ignore if there is a conserving player
     */
    default void forceSendDialogTo(ServerPlayer serverPlayer, CompoundTag data) {
        PacketRelay.sendToPlayer(PacketHandler.INSTANCE, new NPCDialoguePacket(((LivingEntity) this).getId(), data), serverPlayer);
        this.setConversingPlayer(serverPlayer);
    }

    /**
     * @return true if there is no conserving player
     */
    default boolean sendDialogTo(ServerPlayer serverPlayer) {
        return sendDialogTo(serverPlayer, new CompoundTag());
    }

    @Nullable
    Player getConversingPlayer();

}
