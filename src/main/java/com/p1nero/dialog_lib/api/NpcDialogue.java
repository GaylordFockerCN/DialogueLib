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

public interface NpcDialogue {
    @OnlyIn(Dist.CLIENT)
    void openDialogueScreen(CompoundTag senderData);

    void handleNpcInteraction(ServerPlayer player, int interactionID);

    void setConversingPlayer(@Nullable Player player);

    default void sendDialogTo(ServerPlayer serverPlayer, CompoundTag data) {
        PacketRelay.sendToPlayer(PacketHandler.INSTANCE, new NPCDialoguePacket(((LivingEntity) this).getId(), data), serverPlayer);
    }

    default void sendDialogTo(ServerPlayer serverPlayer) {
        sendDialogTo(serverPlayer, new CompoundTag());
    }

    @Nullable
    Player getConversingPlayer();

}
