package com.p1nero.dialog_lib.capability;

import com.p1nero.dialog_lib.DialogueLib;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = DialogueLib.MOD_ID)
public class DialogCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<DialogPlayerData> DIALOG_PLAYER = CapabilityManager.get(new CapabilityToken<>() {});

    private DialogPlayerData DialogPlayerData = null;

    private final LazyOptional<DialogPlayerData> optional = LazyOptional.of(this::createPlayerData);

    private DialogPlayerData createPlayerData() {
        if(this.DialogPlayerData == null){
            this.DialogPlayerData = new DialogPlayerData();
        }

        return this.DialogPlayerData;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == DIALOG_PLAYER){
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createPlayerData().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createPlayerData().loadNBTData(tag);
    }

    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if(!player.getCapability(DialogCapabilityProvider.DIALOG_PLAYER).isPresent()){
                event.addCapability(ResourceLocation.fromNamespaceAndPath(DialogueLib.MOD_ID, "dialog_player_data"), new DialogCapabilityProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        if(event.isWasDeath()) {
            event.getOriginal().getCapability(DialogCapabilityProvider.DIALOG_PLAYER).ifPresent(oldStore -> {
                event.getEntity().getCapability(DialogCapabilityProvider.DIALOG_PLAYER).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                    newStore.syncToClient(((ServerPlayer) event.getEntity()));
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase.equals(TickEvent.Phase.END)) {
            getDialogPlayer(event.player).tick(event.player);
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(DialogPlayerData.class);
    }

    public static DialogPlayerData getDialogPlayer(Player player) {
        if(player == null) {
            return new DialogPlayerData();
        }
        return player.getCapability(DialogCapabilityProvider.DIALOG_PLAYER).orElse(new DialogPlayerData());
    }

    public static void syncPlayerDataToClient(ServerPlayer serverPlayer) {
        getDialogPlayer(serverPlayer).syncToClient(serverPlayer);
    }

}
