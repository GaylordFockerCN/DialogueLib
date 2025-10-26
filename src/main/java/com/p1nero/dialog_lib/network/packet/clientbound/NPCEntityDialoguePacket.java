package com.p1nero.dialog_lib.network.packet.clientbound;
import com.p1nero.dialog_lib.api.custom.IEntityNpc;
import com.p1nero.dialog_lib.events.ClientNpcEntityDialogueEvent;
import com.p1nero.dialog_lib.network.packet.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

public record NPCEntityDialoguePacket(int id, CompoundTag data) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.id());
        buf.writeNbt(this.data());
    }

    public static NPCEntityDialoguePacket decode(FriendlyByteBuf buf) {
        int id = buf.readInt();
        CompoundTag tag = buf.readNbt();
        return new NPCEntityDialoguePacket(id,tag);
    }

    @Override
    public void execute(Player playerEntity) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            Entity entity = Minecraft.getInstance().level.getEntity(this.id());
            if(entity == null) {
                return;
            }
            ClientNpcEntityDialogueEvent event = new ClientNpcEntityDialogueEvent(entity, Minecraft.getInstance().player, data);
            MinecraftForge.EVENT_BUS.post(event);
            if(!event.isCanceled()) {
                if (entity instanceof IEntityNpc npc) {
                    npc.setConversingPlayer(playerEntity);
                    npc.openDialogueScreen(this.data());
                }
                ClientBoundHandler.openDialogScreen(entity, data);
            }
        }
    }
}