package net.p1nero.dialogue_lib.network;

import net.p1nero.dialogue_lib.DialogueLib;
import net.p1nero.dialogue_lib.network.packet.BasePacket;
import net.p1nero.dialogue_lib.network.packet.NPCDialoguePacket;
import net.p1nero.dialogue_lib.network.packet.NpcPlayerInteractPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DialogueLib.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    private static int index;

    public static synchronized void register() {
        // CLIENTBOUND
         register(NPCDialoguePacket.class, NPCDialoguePacket::decode);


        // SERVERBOUND
        register(NpcPlayerInteractPacket.class, NpcPlayerInteractPacket::decode);

        // BOTH

    }

    private static <MSG extends BasePacket> void register(final Class<MSG> packet, Function<FriendlyByteBuf, MSG> decoder) {
        INSTANCE.messageBuilder(packet, index++).encoder(BasePacket::encode).decoder(decoder).consumerMainThread(BasePacket::handle).add();
    }
}
