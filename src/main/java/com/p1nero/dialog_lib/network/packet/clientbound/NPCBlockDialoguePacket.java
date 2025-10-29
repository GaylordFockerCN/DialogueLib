package com.p1nero.dialog_lib.network.packet.clientbound;

import com.p1nero.dialog_lib.api.block.custom.IBlockEntityNpc;
import com.p1nero.dialog_lib.events.ClientNpcBlockDialogueEvent;
import com.p1nero.dialog_lib.network.packet.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
            BlockState blockState = Minecraft.getInstance().level.getBlockState(pos);
            ClientNpcBlockDialogueEvent event = new ClientNpcBlockDialogueEvent(pos, blockState, Minecraft.getInstance().player, data);
            MinecraftForge.EVENT_BUS.post(event);
            if(!event.isCanceled()) {
                BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
                if (blockEntity instanceof IBlockEntityNpc npc) {
                    npc.openDialogueScreen(this.data());
                }
                ClientBoundHandler.openBlockDialogScreen(blockState, pos, data);
            }
        }
    }
}