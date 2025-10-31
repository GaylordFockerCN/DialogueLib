package com.p1nero.dialog_lib.network.packet.clientbound;

import com.p1nero.dialog_lib.DialogueLib;
import com.p1nero.dialog_lib.capability.DialogueLibCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

public class ClientBoundHandler {

    public static void openEntityDialogScreen(Entity entity, CompoundTag data) {
        DialogueLib.runIfEntityExtensionExist(Minecraft.getInstance().player, entity, (iEntityDialogueExtension -> {
            iEntityDialogueExtension.openDialogScreen(Minecraft.getInstance().player, entity, data);
        }));
    }

    public static void openBlockDialogScreen(BlockState blockState, BlockPos pos, CompoundTag data) {
        DialogueLib.runIfBlockExtensionExist(Minecraft.getInstance().player, blockState, pos, (iBlockDialogueExtension -> {
            iBlockDialogueExtension.openDialogScreen(Minecraft.getInstance().player, blockState, pos, data);
        }));
    }

    public static void syncPlayerData(int id, CompoundTag data) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            Entity entity = Minecraft.getInstance().level.getEntity(id);
            if(entity != null){
                DialogueLibCapabilities.getDialogPatch(entity).loadNBTData(data);
            }
        }
    }

}
