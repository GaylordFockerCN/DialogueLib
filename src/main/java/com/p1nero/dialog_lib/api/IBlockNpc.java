package com.p1nero.dialog_lib.api;

import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.clientbound.NPCBlockDialoguePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 可对话的方块
 */
public interface IBlockNpc {
    @OnlyIn(Dist.CLIENT)
    void openDialogueScreen(CompoundTag senderData);
    void handleNpcInteraction(Player player, int interactionID);
    default void sendDialogueTo(ServerPlayer serverPlayer, CompoundTag data) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCBlockDialoguePacket(((BlockEntity) this).getBlockPos(), data), serverPlayer);
    }
    default void sendDialogueTo(ServerPlayer serverPlayer) {
        sendDialogueTo(serverPlayer, new CompoundTag());
    }
}
