package com.p1nero.dialog_lib.network.packet.clientbound;

import com.p1nero.dialog_lib.DialogueLib;
import com.p1nero.dialog_lib.capability.DialogCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class ClientBoundHandler {

    public static void openDialogScreen(Entity entity, CompoundTag data) {
        DialogueLib.runIfExtensionExist(Minecraft.getInstance().player, entity, (iEntityDialogueExtension -> {
            iEntityDialogueExtension.openDialogScreen(Minecraft.getInstance().player, entity, data);
        }));
    }

    public static void syncPlayerData(CompoundTag data) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            DialogCapabilities.getDialogPatch(Minecraft.getInstance().player).loadNBTData(data);
        }
    }

}
