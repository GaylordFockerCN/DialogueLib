package com.p1nero.dialog_lib.network.packet.serverbound;

import com.p1nero.dialog_lib.api.NpcDialogue;
import com.p1nero.dialog_lib.network.packet.BasePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * This packet is sent to the server whenever the player chooses an important action in the NPC dialogue.
 */
public record NpcPlayerInteractPacket(int entityID, byte interactionID) implements BasePacket {
    public static final int NO_ENTITY = -1;

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityID());
        buf.writeByte(this.interactionID());
    }

    public static NpcPlayerInteractPacket decode(FriendlyByteBuf buf) {
        return new NpcPlayerInteractPacket(buf.readInt(), buf.readByte());
    }

    @Override
    public void execute(@Nullable Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            Entity entity = player.level().getEntity(this.entityID());
            if (entity instanceof NpcDialogue npc){
                npc.handleNpcInteraction(serverPlayer, this.interactionID());
            }
        }
    }
}
