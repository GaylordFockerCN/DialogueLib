package com.p1nero.dialog_lib.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ServerCustomInteractEvent extends Event {
    private final ServerPlayer serverPlayer;
    private final int interactId;

    public ServerCustomInteractEvent(ServerPlayer serverPlayer, int interactId) {
        this.serverPlayer = serverPlayer;
        this.interactId = interactId;
    }

    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    public int getInteractId() {
        return interactId;
    }

}
