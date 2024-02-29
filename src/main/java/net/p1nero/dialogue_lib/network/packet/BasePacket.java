//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.p1nero.dialogue_lib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface BasePacket {
    void encode(FriendlyByteBuf var1);

    default boolean handle(Supplier<NetworkEvent.Context> context) {
        ((NetworkEvent.Context)context.get()).enqueueWork(() -> {
            this.execute(((NetworkEvent.Context)context.get()).getSender());
        });
        return true;
    }

    void execute(Player var1);
}
