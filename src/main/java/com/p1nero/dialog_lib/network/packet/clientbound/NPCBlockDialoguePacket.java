package com.p1nero.dialog_lib.network.packet.clientbound;
import com.p1nero.dialog_lib.api.INpcDialogueBlock;
import com.p1nero.dialog_lib.network.packet.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record NPCBlockDialoguePacket(BlockPos pos, CompoundTag tag) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos());
        buf.writeNbt(this.tag());
    }

    public static NPCBlockDialoguePacket decode(FriendlyByteBuf buf) {
        return new NPCBlockDialoguePacket(buf.readBlockPos(), buf.readNbt());
    }

    @Override
    public void execute(Player playerEntity) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof INpcDialogueBlock npc) {
                npc.openDialogueScreen(this.tag());
            }
        }
    }
}