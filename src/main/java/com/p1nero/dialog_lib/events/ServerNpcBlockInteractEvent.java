package com.p1nero.dialog_lib.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ServerNpcBlockInteractEvent extends Event {
    private final BlockPos pos;
    private final BlockState self;
    private final ServerPlayer serverPlayer;
    private final int interactId;

    public ServerNpcBlockInteractEvent(BlockPos pos, BlockState self, ServerPlayer serverPlayer, int interactId) {
        this.self = self;
        this.serverPlayer = serverPlayer;
        this.pos = pos;
        this.interactId = interactId;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    public BlockState getSelf() {
        return self;
    }

    public int getInteractId() {
        return interactId;
    }
}
