package com.p1nero.dialog_lib.capability;

import com.p1nero.dialog_lib.DialogueLib;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = DialogueLib.MOD_ID)
public class DialogCapabilities implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<DialogEntityPatch> DIALOG_ENTITY = CapabilityManager.get(new CapabilityToken<>() {});

    private DialogEntityPatch DialogEntityPatch = null;

    private final LazyOptional<DialogEntityPatch> optional = LazyOptional.of(this::createDialogData);

    private DialogEntityPatch createDialogData() {
        if(this.DialogEntityPatch == null){
            this.DialogEntityPatch = new DialogEntityPatch();
        }

        return this.DialogEntityPatch;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == DIALOG_ENTITY){
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createDialogData().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createDialogData().loadNBTData(tag);
    }

    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() != null && DialogueLib.EXTENSIONS_MAP.containsKey(event.getObject().getType())) {
            if(!event.getObject().getCapability(DialogCapabilities.DIALOG_ENTITY).isPresent()){
                event.addCapability(ResourceLocation.fromNamespaceAndPath(DialogueLib.MOD_ID, "dialog_entity_data"), new DialogCapabilities());
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(DialogEntityPatch.class);
    }

    public static DialogEntityPatch getDialogPatch(Entity entity) {
        if(entity == null) {
            return new DialogEntityPatch();
        }
        return entity.getCapability(DialogCapabilities.DIALOG_ENTITY).orElse(new DialogEntityPatch());
    }

    @Nullable
    public static ServerPlayer getConservingPlayer(Entity entity) {
        return getDialogPatch(entity).getCurrentTalkingPlayer();
    }

}
