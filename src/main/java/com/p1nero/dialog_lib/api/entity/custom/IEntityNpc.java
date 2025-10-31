package com.p1nero.dialog_lib.api.entity.custom;

import com.p1nero.dialog_lib.api.entity.goal.LookAtConservingPlayerGoal;
import com.p1nero.dialog_lib.capability.DialogueLibCapabilities;
import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.clientbound.NPCEntityDialoguePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;

import javax.annotation.Nullable;

/**
 * 自己写的实体需要实现此接口，并在合适的时候发包（比如mob Interact等时候）
 */
public interface IEntityNpc {

    default Entity asEntity() {
        return (Entity) this;
    }

    /**
     * 是否看向对话中的玩家，等效于 {@link LookAtConservingPlayerGoal}
     * 在 {@link com.p1nero.dialog_lib.DialogueLib#dialog_lib$onLivingTick(LivingEvent.LivingTickEvent)} 实现
     */
    default boolean shouldLookAtConservingPlayer() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    default void openDialogueScreen(CompoundTag senderData) {
        DialogueScreen dialogueScreen = getDialogueScreen(senderData);
        if (dialogueScreen != null) {
            Minecraft.getInstance().setScreen(dialogueScreen);
        }
    }

    @OnlyIn(Dist.CLIENT)
    DialogueScreen getDialogueScreen(CompoundTag senderData);

    /**
     * 处理客户端返回值，具体返回值在对话构建中实现。
     */
    void handleNpcInteraction(ServerPlayer player, int interactionID);

    default void setConversingPlayer(@Nullable Player player) {
        DialogueLibCapabilities.setConservingPlayer(asEntity(), player);
    }

    @Nullable
    default Player getConversingPlayer() {
        return DialogueLibCapabilities.getConservingPlayer(asEntity());
    }

    /**
     * 向客户端发起对话请求。可以在mobInteract里或者某些情况下调用
     *
     * @param data 传递给客户端的数据 {@link IEntityNpc#openDialogueScreen(CompoundTag)}
     */
    default void sendDialogTo(ServerPlayer serverPlayer, CompoundTag data) {
        if (this.getConversingPlayer() == null) {
            DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCEntityDialoguePacket(((LivingEntity) this).getId(), data), serverPlayer);
            this.setConversingPlayer(serverPlayer);
        }
    }

    /**
     * 不检查是否有对话中的玩家，强制发起对话
     */
    default void forceSendDialogTo(ServerPlayer serverPlayer, CompoundTag data) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCEntityDialoguePacket(((LivingEntity) this).getId(), data), serverPlayer);
        this.setConversingPlayer(serverPlayer);
    }

    default void sendDialogTo(ServerPlayer serverPlayer) {
        sendDialogTo(serverPlayer, this.getServerData());
    }

    default CompoundTag getServerData() {
        return new CompoundTag();
    }

}
