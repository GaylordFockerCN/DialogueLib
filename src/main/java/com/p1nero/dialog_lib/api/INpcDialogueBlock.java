package com.p1nero.dialog_lib.api;

import com.p1nero.dialog_lib.network.PacketHandler;
import com.p1nero.dialog_lib.network.PacketRelay;
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
public interface INpcDialogueBlock {
    @OnlyIn(Dist.CLIENT)
    void openDialogueScreen(CompoundTag senderData);
    void handleNpcInteraction(Player player, int interactionID);
    default void sendDialogueTo(ServerPlayer serverPlayer, CompoundTag data) {
        PacketRelay.sendToPlayer(PacketHandler.INSTANCE, new NPCBlockDialoguePacket(((BlockEntity) this).getBlockPos(), data), serverPlayer);
    }
    default void sendDialogueTo(ServerPlayer serverPlayer) {
        sendDialogueTo(serverPlayer, new CompoundTag());
    }
}
