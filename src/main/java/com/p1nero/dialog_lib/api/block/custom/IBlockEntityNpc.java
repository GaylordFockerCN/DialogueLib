package com.p1nero.dialog_lib.api.block.custom;

import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.clientbound.NPCBlockDialoguePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 可对话的方块实体
 */
public interface IBlockEntityNpc {

    @OnlyIn(Dist.CLIENT)
    default void openDialogueScreen(CompoundTag senderData) {
        DialogueScreen dialogueScreen = getDialogueScreen(senderData);
        if (dialogueScreen != null) {
            Minecraft.getInstance().setScreen(dialogueScreen);
        }
    }
    @OnlyIn(Dist.CLIENT)
    DialogueScreen getDialogueScreen(CompoundTag senderData);

    void handleNpcInteraction(ServerPlayer player, int interactionID);

    default void sendDialogueTo(ServerPlayer serverPlayer, CompoundTag data) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCBlockDialoguePacket(((BlockEntity) this).getBlockPos(), data), serverPlayer);
    }

    default void sendDialogueTo(ServerPlayer serverPlayer) {
        sendDialogueTo(serverPlayer, getServerData());
    }

    default CompoundTag getServerData() {
        return new CompoundTag();
    }

}
