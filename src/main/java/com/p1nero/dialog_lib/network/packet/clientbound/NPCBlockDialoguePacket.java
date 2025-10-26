package com.p1nero.dialog_lib.network.packet.clientbound;

import com.p1nero.dialog_lib.api.custom.IBlockNpc;
import com.p1nero.dialog_lib.events.ClientNpcBlockDialogueEvent;
import com.p1nero.dialog_lib.network.packet.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;

public record NPCBlockDialoguePacket(BlockPos pos, CompoundTag data) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos());
        buf.writeNbt(this.data());
    }

    public static NPCBlockDialoguePacket decode(FriendlyByteBuf buf) {
        return new NPCBlockDialoguePacket(buf.readBlockPos(), buf.readNbt());
    }

    @Override
    public void execute(Player playerEntity) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
            if(blockEntity == null) {
                return;
            }
            ClientNpcBlockDialogueEvent event = new ClientNpcBlockDialogueEvent(pos, blockEntity, Minecraft.getInstance().player, data);
            MinecraftForge.EVENT_BUS.post(event);
            if(!event.isCanceled()) {
                if (blockEntity instanceof IBlockNpc npc) {
                    npc.openDialogueScreen(this.data());
                }
            }
        }
    }
}