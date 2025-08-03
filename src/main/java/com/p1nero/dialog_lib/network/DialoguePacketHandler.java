package com.p1nero.dialog_lib.network;

import com.p1nero.dialog_lib.DialogueLib;
import com.p1nero.dialog_lib.network.packet.BasePacket;
import com.p1nero.dialog_lib.network.packet.clientbound.NPCBlockDialoguePacket;
import com.p1nero.dialog_lib.network.packet.clientbound.NPCEntityDialoguePacket;
import com.p1nero.dialog_lib.network.packet.serverbound.NpcBlockPlayerInteractPacket;
import com.p1nero.dialog_lib.network.packet.serverbound.NpcEntityPlayerInteractPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;

public class DialoguePacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(DialogueLib.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    private static int index;

    public static synchronized void register() {
        register(NPCEntityDialoguePacket.class, NPCEntityDialoguePacket::decode);
        register(NPCBlockDialoguePacket.class, NPCBlockDialoguePacket::decode);
        register(NpcEntityPlayerInteractPacket.class, NpcEntityPlayerInteractPacket::decode);
        register(NpcBlockPlayerInteractPacket.class, NpcBlockPlayerInteractPacket::decode);
    }

    private static <MSG extends BasePacket> void register(final Class<MSG> packet, Function<FriendlyByteBuf, MSG> decoder) {
        INSTANCE.messageBuilder(packet, index++).encoder(BasePacket::encode).decoder(decoder).consumerMainThread(BasePacket::handle).add();
    }
}
