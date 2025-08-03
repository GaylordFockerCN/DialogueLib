package com.p1nero.dialog_lib.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ServerNpcEntityInteractEvent extends Event {
    private final Entity self;
    private final ServerPlayer serverPlayer;
    private final int interactId;

    public ServerNpcEntityInteractEvent(Entity self, ServerPlayer serverPlayer, int interactId) {
        this.self = self;
        this.serverPlayer = serverPlayer;
        this.interactId = interactId;
    }

    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    public Entity getSelf() {
        return self;
    }

    public int getInteractId() {
        return interactId;
    }
}
