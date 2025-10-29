package com.p1nero.dialog_lib.api.block;

import com.p1nero.dialog_lib.DialogueLib;
import com.p1nero.dialog_lib.api.entity.EntityDialogueExtension;
import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import com.p1nero.dialog_lib.client.screen.builder.DialogueScreenBuilder;
import com.p1nero.dialog_lib.client.screen.builder.StreamDialogueScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IBlockDialogueExtension<T extends Block> {

    default String getModId() {
        return this.getClass().getAnnotation(EntityDialogueExtension.class).modId();
    }

    T getBlock();

    /**
     * 判断是否可以和某玩家发起对话。以下所有的行为都在此基础上进行，无需额外判断。
     */
    boolean canInteractWith(Player player, BlockState currentTalking, BlockPos pos);

    /**
     * 是否取消原先的交互
     */
    default boolean shouldCancelInteract(Player player, BlockState currentTalking, BlockPos pos, InteractionHand hand) {
        return true;
    }

    /**
     * 和方块交互时触发，默认如果无对话中的实体则直接发送对话请求
     */
    default void onPlayerInteract(Player player, BlockState currentTalking, BlockPos pos, InteractionHand hand) {
        if(player instanceof ServerPlayer serverPlayer) {
            DialogueLib.sendDialog(pos, getServerData(serverPlayer, currentTalking, pos, hand, new CompoundTag()), serverPlayer);
        }
    }

    /**
     * 来自服务端的数据
     */
    default CompoundTag getServerData(ServerPlayer player, BlockState currentTalking, BlockPos pos, InteractionHand hand, CompoundTag senderData) {
        return senderData;
    }


    @OnlyIn(Dist.CLIENT)
    default void openDialogScreen(LocalPlayer localPlayer, BlockState currentTalking, BlockPos pos, CompoundTag senderData){
        StreamDialogueScreenBuilder dialogueScreenBuilder = new StreamDialogueScreenBuilder(currentTalking, pos, this.getModId());
        DialogueScreen dialogueScreen = getDialogScreen(dialogueScreenBuilder, localPlayer, currentTalking, pos, senderData);
        if(dialogueScreen != null) {
            Minecraft.getInstance().setScreen(dialogueScreen);
        }
    }

    /**
     * 在这里构造你的对话
     */
    @OnlyIn(Dist.CLIENT)
    DialogueScreen getDialogScreen(StreamDialogueScreenBuilder builder, LocalPlayer localPlayer, BlockState currentTalking, BlockPos pos, CompoundTag senderData);

    /**
     * 处理客户端的返回值
     * interactionId 为 0 时将清除对话中的玩家
     */
    void handleNpcInteraction(BlockState currentTalking, BlockPos pos, ServerPlayer player, int interactionId);

}
