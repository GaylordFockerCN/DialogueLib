package com.p1nero.dialog_lib.network;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import net.minecraftforge.network.simple.SimpleChannel;

public class DialoguePacketRelay {

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        sendToPlayer(DialoguePacketHandler.INSTANCE, message, player);
    }

    public static <MSG> void sendToNear(MSG message, double x, double y, double z, double radius, ResourceKey<Level> dimension) {
        sendToNear(DialoguePacketHandler.INSTANCE, message, x, y, z, radius, dimension);
    }

    public static <MSG> void sendToAll(MSG message) {
        sendToAll(DialoguePacketHandler.INSTANCE, message);
    }

    public static <MSG> void sendToServer(MSG message) {
        sendToServer(DialoguePacketHandler.INSTANCE, message);
    }

    public static <MSG> void sendToDimension(MSG message, ResourceKey<Level> dimension) {
        sendToDimension(DialoguePacketHandler.INSTANCE, message, dimension);
    }

    public static <MSG> void sendToPlayer(SimpleChannel handler, MSG message, ServerPlayer player) {
        handler.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToNear(SimpleChannel handler, MSG message, double x, double y, double z, double radius, ResourceKey<Level> dimension) {
        handler.send(PacketDistributor.NEAR.with(TargetPoint.p(x, y, z, radius, dimension)), message);
    }

    public static <MSG> void sendToAll(SimpleChannel handler, MSG message) {
        handler.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToServer(SimpleChannel handler, MSG message) {
        handler.sendToServer(message);
    }

    public static <MSG> void sendToDimension(SimpleChannel handler, MSG message, ResourceKey<Level> dimension) {
        handler.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

}
