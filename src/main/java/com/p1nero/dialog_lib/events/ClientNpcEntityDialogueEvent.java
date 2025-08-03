package com.p1nero.dialog_lib.events;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ClientNpcEntityDialogueEvent extends Event {
    private final Entity self;
    private final LocalPlayer localPlayer;
    private final CompoundTag serverData;
    public ClientNpcEntityDialogueEvent(Entity self, LocalPlayer localPlayer, CompoundTag serverData) {
        this.self = self;
        this.localPlayer = localPlayer;
        this.serverData = serverData;
    }

    public Entity getSelf() {
        return self;
    }

    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }

    public CompoundTag getServerData() {
        return serverData;
    }
}
