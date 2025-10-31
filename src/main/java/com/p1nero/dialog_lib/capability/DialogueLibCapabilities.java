package com.p1nero.dialog_lib.capability;

import com.p1nero.dialog_lib.DialogueLib;
import com.p1nero.dialog_lib.api.entity.custom.IEntityNpc;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.clientbound.SyncDialogPlayerDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = DialogueLib.MOD_ID)
public class DialogueLibCapabilities {

    public static Capability<DialogEntityPatch> DIALOG_ENTITY = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() != null && (DialogueLib.ENTITY_EXTENSIONS_MAP.containsKey(event.getObject().getType()) || event.getObject() instanceof IEntityNpc)) {
            if(!event.getObject().getCapability(DIALOG_ENTITY).isPresent()){
                event.addCapability(ResourceLocation.fromNamespaceAndPath(DialogueLib.MOD_ID, "dialog_entity_data"), new DialogEntityPatchProvider(event.getObject()));
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(DialogEntityPatch.class);
    }

    public static DialogEntityPatch getDialogPatch(Entity entity) {
        if(entity == null) {
            return null;
        }
        return entity.getCapability(DialogueLibCapabilities.DIALOG_ENTITY).orElse(null);
    }

    @Nullable
    public static Player getConservingPlayer(Entity entity) {
        return getDialogPatch(entity).getCurrentTalkingPlayer();
    }

    public static void setConservingPlayer(Entity entity, @Nullable Player player) {
        getDialogPatch(entity).setConservingPlayer(player);
    }

    public static void syncToClient(@NotNull Entity entity) {
        if(entity.level() instanceof ServerLevel) {
            DialoguePacketRelay.sendToAll(DialoguePacketHandler.INSTANCE, new SyncDialogPlayerDataPacket(entity.getId(), getDialogPatch(entity).saveNBTData(new CompoundTag())));
        }
    }

}
