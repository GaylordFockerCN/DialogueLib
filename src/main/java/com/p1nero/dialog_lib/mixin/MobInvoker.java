package com.p1nero.dialog_lib.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mob.class)
public interface MobInvoker {

    @Invoker("getAmbientSound")
    SoundEvent dialog_lib$invokeGetAmbientSound();
}
