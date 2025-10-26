package com.p1nero.dialog_lib.network.packet.clientbound;

import com.p1nero.dialog_lib.network.packet.BasePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record SyncDialogPlayerDataPacket(CompoundTag data) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(data);
    }

    public static SyncDialogPlayerDataPacket decode(FriendlyByteBuf buf) {
        return new SyncDialogPlayerDataPacket(buf.readNbt());
    }

    @Override
    public void execute(Player playerEntity) {
        ClientBoundHandler.syncPlayerData(data);
    }
}