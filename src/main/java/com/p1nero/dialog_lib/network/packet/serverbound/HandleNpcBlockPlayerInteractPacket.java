package com.p1nero.dialog_lib.network.packet.serverbound;

import com.p1nero.dialog_lib.api.IBlockNpc;
import com.p1nero.dialog_lib.events.ServerNpcBlockInteractEvent;
import com.p1nero.dialog_lib.network.packet.BasePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

/**
 * This packet is sent to the server whenever the player chooses an important action in the NPC dialogue.
 */
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
            BlockEntity blockEntity = playerEntity.level().getBlockEntity(pos);
            if(blockEntity != null) {
                ServerNpcBlockInteractEvent event = new ServerNpcBlockInteractEvent(pos, blockEntity, serverPlayer, interactionID);
                MinecraftForge.EVENT_BUS.post(event);
                if(!event.isCanceled()) {
                    if(blockEntity instanceof IBlockNpc npcDialogueBlock) {
                        npcDialogueBlock.handleNpcInteraction(playerEntity, interactionID);
                    }
                }
            }
        }
    }
}
