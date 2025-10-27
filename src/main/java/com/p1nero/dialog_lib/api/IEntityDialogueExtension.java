package com.p1nero.dialog_lib.api;

import com.p1nero.dialog_lib.DialogueLib;
import com.p1nero.dialog_lib.capability.DialogCapabilities;
import com.p1nero.dialog_lib.client.screen.DialogueScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IEntityDialogueExtension<T extends Entity> {

    EntityType<T> getEntityType();

    /**
     * 判断是否可以和某玩家发起对话。以下所有的行为都在此基础上进行，无需额外判断。
     */
    boolean canInteractWith(Player player, T currentTalking);

    /**
     * 若为true，对话时会调用lookControl和navigation（如果存在）
     */
    default boolean shouldLookAtPlayer(ServerPlayer player, T currentTalking) {
        return true;
    }

    /**
     * 最大对话范围，超过则终止对话
     */
    default int maxTalkDistance() {
        return 5;
    }

    /**
     * 是否取消原先的交互
     */
    default boolean shouldCancelInteract(Player player, T currentTalking, InteractionHand hand) {
        return true;
    }

    /**
     * 和实体交互时触发，默认如果无对话中的实体则直接发送对话请求
     */
    default void onPlayerInteract(Player player, T currentTalking, InteractionHand hand) {
        if(player instanceof ServerPlayer serverPlayer && getConservingPlayer(currentTalking) == null) {
            DialogueLib.sendDialog(currentTalking, getServerData(serverPlayer, currentTalking, hand, new CompoundTag()), serverPlayer);
            setConservingPlayer(serverPlayer, currentTalking);
        }
    }

    /**
     * 来自服务端的数据
     */
    default CompoundTag getServerData(ServerPlayer player, T currentTalking, InteractionHand hand, CompoundTag senderData) {
        return senderData;
    }

    @OnlyIn(Dist.CLIENT)
    default void openDialogScreen(LocalPlayer localPlayer, T currentTalking, CompoundTag senderData){
        DialogueScreenBuilder dialogueScreenBuilder = new DialogueScreenBuilder(currentTalking);
        dialogueScreenBuilder = getDialogBuilder(dialogueScreenBuilder, localPlayer, currentTalking, senderData);
        if(dialogueScreenBuilder != null && !dialogueScreenBuilder.isEmpty()) {
            Minecraft.getInstance().setScreen(dialogueScreenBuilder.build());
        }
    }

    /**
     * 在这里构造你的对话
     */
    @OnlyIn(Dist.CLIENT)
    DialogueScreenBuilder getDialogBuilder(DialogueScreenBuilder builder, LocalPlayer localPlayer, T currentTalking, CompoundTag senderData);

    /**
     * 处理客户端的返回值。
     * interactionId 为 0 时将清除对话中的玩家
     */
    void handleNpcInteraction(T currentTalking, ServerPlayer player, int interactionId);

    /**
     * 记录当前玩家对话的实体，可用于控制实体看玩家
     */
    default void setConservingPlayer(ServerPlayer player, T currentTalking) {
        DialogCapabilities.getDialogPatch(currentTalking).setConservingPlayer(player);
    }

    default void onTalkingTick(ServerPlayer player, T currentTalking) {

    }

    /**
     * 移除当前对话的玩家
     */
    default void removeConservingPlayer(T currentTalking) {
        DialogCapabilities.getDialogPatch(currentTalking).setConservingPlayer(null);
    }

    /**
     * 获取当前玩家对话的实体
     */
    default Entity getConservingPlayer(T currentTalking) {
        return DialogCapabilities.getDialogPatch(currentTalking).getCurrentTalkingPlayer();
    }

}
