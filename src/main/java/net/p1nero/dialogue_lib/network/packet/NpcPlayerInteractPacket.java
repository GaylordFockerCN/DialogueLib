package net.p1nero.dialogue_lib.network.packet;

import net.p1nero.dialogue_lib.entity.Dialogueable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * This packet is sent to the server whenever the player chooses an important action in the NPC dialogue.
 */
public record NpcPlayerInteractPacket(int entityID, byte interactionID) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityID());
        buf.writeByte(this.interactionID());
    }

    public static NpcPlayerInteractPacket decode(FriendlyByteBuf buf) {
        return new NpcPlayerInteractPacket(buf.readInt(), buf.readByte());
    }

    @Override
    public void execute(@Nullable Player playerEntity) {
        if (playerEntity != null && playerEntity.getServer() != null && playerEntity.level().getEntity(this.entityID()) instanceof Dialogueable npc) {
            npc.handleNpcInteraction(playerEntity, this.interactionID());
        }
    }
}
