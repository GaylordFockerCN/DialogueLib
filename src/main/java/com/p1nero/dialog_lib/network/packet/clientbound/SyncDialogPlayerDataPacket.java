package com.p1nero.dialog_lib.network.packet.clientbound;

import com.p1nero.dialog_lib.network.packet.BasePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record SyncDialogPlayerDataPacket(int id, CompoundTag data) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeNbt(data);
    }

    public static SyncDialogPlayerDataPacket decode(FriendlyByteBuf buf) {
        return new SyncDialogPlayerDataPacket(buf.readInt(), buf.readNbt());
    }

    @Override
    public void execute(Player playerEntity) {
        ClientBoundHandler.syncPlayerData(id, data);
    }
}