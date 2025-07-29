package com.p1nero.dialog_lib.network.packet.serverbound;

import com.p1nero.dialog_lib.api.INpcDialogueBlock;
import com.p1nero.dialog_lib.network.packet.BasePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

/**
 * This packet is sent to the server whenever the player chooses an important action in the NPC dialogue.
 */
public record NpcBlockPlayerInteractPacket(BlockPos pos, int interactionID) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos());
        buf.writeByte(this.interactionID());
    }

    public static NpcBlockPlayerInteractPacket decode(FriendlyByteBuf buf) {
        return new NpcBlockPlayerInteractPacket(buf.readBlockPos(), buf.readByte());
    }

    @Override
    public void execute(@Nullable Player playerEntity) {
        if (playerEntity != null && playerEntity.getServer() != null) {
            BlockEntity blockEntity = playerEntity.level().getBlockEntity(pos);
            if(blockEntity instanceof INpcDialogueBlock npcDialogueBlock) {
                npcDialogueBlock.handleNpcInteraction(playerEntity, interactionID);
            }
        }
    }
}
