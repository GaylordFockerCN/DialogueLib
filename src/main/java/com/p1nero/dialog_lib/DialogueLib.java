package com.p1nero.dialog_lib;

import com.mojang.logging.LogUtils;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.clientbound.NPCEntityDialoguePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DialogueLib.MOD_ID)
public class DialogueLib {

    public static final String MOD_ID = "p1nero_dl";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DialogueLib(FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener(this::commonSetup);
        context.registerConfig(ModConfig.Type.CLIENT, DialogueLibConfig.SPEC);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        DialoguePacketHandler.register();
    }

    public static void sendDialog(LivingEntity self, CompoundTag data, ServerPlayer player) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCEntityDialoguePacket(self.getId(), data), player);
    }

    public static void sendDialog(LivingEntity self, ServerPlayer player) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCEntityDialoguePacket(self.getId(), new CompoundTag()), player);
    }

}
