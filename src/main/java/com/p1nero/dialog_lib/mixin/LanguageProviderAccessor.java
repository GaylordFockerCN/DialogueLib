package com.p1nero.dialog_lib.mixin;

import net.minecraftforge.common.data.LanguageProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LanguageProvider.class)
public interface LanguageProviderAccessor {

    @Accessor(value = "modid", remap = false)
    String getModId();
}
