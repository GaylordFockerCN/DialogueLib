package com.p1nero.dialog_lib.network.packet.serverbound;

import com.p1nero.dialog_lib.DialogueLib;
import com.p1nero.dialog_lib.api.block.custom.IBlockEntityNpc;
import com.p1nero.dialog_lib.events.ServerNpcBlockInteractEvent;
import com.p1nero.dialog_lib.network.packet.BasePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public record HandleNpcBlockPlayerInteractPacket(BlockPos pos, int interactionID) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos());
        buf.writeByte(this.interactionID());
    }

    public static HandleNpcBlockPlayerInteractPacket decode(FriendlyByteBuf buf) {
        return new HandleNpcBlockPlayerInteractPacket(buf.readBlockPos(), buf.readByte());
    }

    @Override
    public void execute(@Nullable Player playerEntity) {
        if (playerEntity instanceof ServerPlayer serverPlayer) {
            BlockState blockState = serverPlayer.serverLevel().getBlockState(pos);
            ServerNpcBlockInteractEvent event = new ServerNpcBlockInteractEvent(pos, blockState, serverPlayer, interactionID);
            MinecraftForge.EVENT_BUS.post(event);
            if(!event.isCanceled()) {
                BlockEntity blockEntity = playerEntity.level().getBlockEntity(pos);
                if(blockEntity != null) {
                    if(blockEntity instanceof IBlockEntityNpc npcDialogueBlock) {
                        npcDialogueBlock.handleNpcInteraction(serverPlayer, interactionID);
                    }
                }
                DialogueLib.runIfBlockExtensionExist(serverPlayer, blockState, pos, (iBlockDialogueExtension -> {
                    iBlockDialogueExtension.handleNpcInteraction(blockState, pos, serverPlayer, this.interactionID());
                }));
            }
        }
    }
}
