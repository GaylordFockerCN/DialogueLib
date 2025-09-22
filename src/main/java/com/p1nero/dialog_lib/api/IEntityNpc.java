package com.p1nero.dialog_lib.api;

import com.p1nero.dialog_lib.client.screen.DialogueScreenBuilder;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.clientbound.NPCEntityDialoguePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * 需要实现此接口，并在合适的时候发包（比如mob Interact等时候）
 * 因为mobInteract情况过于复杂，于是本lib就不代理开启对话了
 */
public interface IEntityNpc {

    @OnlyIn(Dist.CLIENT)
    default void openDialogueScreen(CompoundTag senderData){
        DialogueScreenBuilder dialogueScreenBuilder = getDialogueBuilder(senderData);
        if(dialogueScreenBuilder != null && !dialogueScreenBuilder.isEmpty()) {
            Minecraft.getInstance().setScreen(dialogueScreenBuilder.build());
        }
    }

    @OnlyIn(Dist.CLIENT)
    DialogueScreenBuilder getDialogueBuilder(CompoundTag senderData);

    /**
     * handle the callback. If the interactionId is 0, it will set conserving player null after talk.
     */
    void handleNpcInteraction(ServerPlayer player, int interactionID);

    void setConversingPlayer(@Nullable Player player);

    /**
     * @param data the server data that use in {@link IEntityNpc#openDialogueScreen(CompoundTag)}
     * @return true if there is no conserving player
     */
    default boolean sendDialogTo(ServerPlayer serverPlayer, CompoundTag data) {
        if(this.getConversingPlayer() == null) {
            DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCEntityDialoguePacket(((LivingEntity) this).getId(), data), serverPlayer);
            this.setConversingPlayer(serverPlayer);
            return true;
        }
        return false;
    }

    default void forceSendDialogTo(ServerPlayer serverPlayer, CompoundTag data) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCEntityDialoguePacket(((LivingEntity) this).getId(), data), serverPlayer);
        this.setConversingPlayer(serverPlayer);
    }

    default boolean sendDialogTo(ServerPlayer serverPlayer) {
        return sendDialogTo(serverPlayer, new CompoundTag());
    }

    @Nullable
    Player getConversingPlayer();

}
