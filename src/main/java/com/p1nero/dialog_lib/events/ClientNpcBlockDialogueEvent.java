package com.p1nero.dialog_lib.events;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ClientNpcBlockDialogueEvent extends Event {
    private final BlockEntity self;
    private final LocalPlayer localPlayer;
    private final BlockPos pos;
    private final CompoundTag serverData;
    public ClientNpcBlockDialogueEvent(BlockPos pos, BlockEntity self, LocalPlayer localPlayer, CompoundTag serverData) {
        this.self = self;
        this.localPlayer = localPlayer;
        this.pos = pos;
        this.serverData = serverData;
    }

    public BlockEntity getSelf() {
        return self;
    }

    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }

    public BlockPos getPos() {
        return pos;
    }

    public CompoundTag getServerData() {
        return serverData;
    }
}
