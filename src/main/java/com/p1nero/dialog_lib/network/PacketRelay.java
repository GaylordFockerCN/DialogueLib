package com.p1nero.dialog_lib.network;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketRelay {
    public PacketRelay() {
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
        handler.send(PacketDistributor.DIMENSION.with(() -> {
            return dimension;
        }), message);
    }

//    public static void sendDelay(int delayMillis, Runnable runnable){
//        new Thread(()->{
//            try {
//                Thread.sleep(delayMillis);//等两端实体数据互通完才能进行同步操作
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            runnable.run();
//        }).start();
//
//    }

}
