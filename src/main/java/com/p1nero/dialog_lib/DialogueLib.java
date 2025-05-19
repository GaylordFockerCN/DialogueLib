package com.p1nero.dialog_lib;

import com.mojang.logging.LogUtils;
import com.p1nero.dialog_lib.network.PacketHandler;
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
        context.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.register();
    }
}
