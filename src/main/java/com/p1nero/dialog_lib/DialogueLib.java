package com.p1nero.dialog_lib;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.p1nero.dialog_lib.api.IEntityDialogueExtension;
import com.p1nero.dialog_lib.capability.DialogCapabilities;
import com.p1nero.dialog_lib.capability.DialogEntityPatch;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.clientbound.NPCEntityDialoguePacket;
import com.p1nero.dialog_lib.util.AnnotatedInstanceUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mod(DialogueLib.MOD_ID)
public class DialogueLib {

    public static final String MOD_ID = "p1nero_dl";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static List<IEntityDialogueExtension> EXTENSIONS = Lists.newArrayList();
    public static Map<EntityType<?>, List<IEntityDialogueExtension>> EXTENSIONS_MAP = new HashMap<>();

    public DialogueLib(FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener(this::dialog_lib$commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::dialog_lib$onEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(this::dialog_lib$onLivingTick);
        context.registerConfig(ModConfig.Type.CLIENT, DialogueLibConfig.SPEC);
    }

    private void dialog_lib$commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(DialoguePacketHandler::register);
        event.enqueueWork(() -> {
            EXTENSIONS = AnnotatedInstanceUtil.getModExtensions();
            for (IEntityDialogueExtension extension : EXTENSIONS) {
                EntityType<?> entityType = extension.getEntityType();
                EXTENSIONS_MAP.computeIfAbsent(entityType, k -> new ArrayList<>()).add(extension);
            }
        });
    }

    private void dialog_lib$onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        runIfExtensionExist(event.getEntity(), event.getTarget(), (iEntityDialogueExtension -> {
            iEntityDialogueExtension.onPlayerInteract(event.getEntity(), event.getTarget(), event.getHand());
            if(iEntityDialogueExtension.shouldCancelInteract(event.getEntity(), event.getTarget())) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }));
    }

    /**
     * 自动打上goal
     */
    private void dialog_lib$onLivingTick(LivingEvent.LivingTickEvent event) {
        if(EXTENSIONS_MAP.containsKey(event.getEntity().getType())) {
            DialogEntityPatch patch = DialogCapabilities.getDialogPatch(event.getEntity());
            ServerPlayer serverPlayer = patch.getCurrentTalkingPlayer();
            if(serverPlayer != null) {
                runIfExtensionExist(serverPlayer, event.getEntity(), iEntityDialogueExtension -> {
                    if(event.getEntity().distanceTo(serverPlayer) > iEntityDialogueExtension.maxTalkDistance()) {
                        patch.setConservingPlayer(null);
                    }
                    if(event.getEntity() instanceof Mob mob) {
                        mob.getLookControl().setLookAt(serverPlayer);
                        mob.getNavigation().stop();
                    }
                });
            }
        }
    }

    public static void runIfExtensionExist(Player player, Entity self, Consumer<IEntityDialogueExtension> extensionConsumer) {
        if(self == null) {
            return;
        }
        if(EXTENSIONS_MAP.containsKey(self.getType())) {
            EXTENSIONS_MAP.get(self.getType()).forEach(dialogueExtension -> {
                if(dialogueExtension.canInteractWith(player, self)) {
                    extensionConsumer.accept(dialogueExtension);
                }
            });
        }
    }

    public static void sendDialog(Entity self, CompoundTag data, ServerPlayer player) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCEntityDialoguePacket(self.getId(), data), player);
    }

    public static void sendDialog(Entity self, ServerPlayer player) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCEntityDialoguePacket(self.getId(), new CompoundTag()), player);
    }

}
