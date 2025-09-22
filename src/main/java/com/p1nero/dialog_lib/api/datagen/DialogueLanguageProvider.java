package com.p1nero.dialog_lib.api.datagen;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public interface DialogueLanguageProvider {
    default LanguageProvider self() {
        return (LanguageProvider)this;
    }

    default void addDialogAnswer(EntityType<?> entity, int i, String text) {
        self().add(entity + ".answer" + i, text);
    }

    default void addDialogAnswer(RegistryObject<? extends EntityType<?>> entity, int i, String text) {
        self().add(entity.get() + ".answer" + i, text);
    }

    default void addDialogAnswer(RegistryObject<? extends EntityType<?>> entity, String answer, String text) {
        self().add(entity.get() + ".answer" + answer, text);
    }

    default void addDialogOption(EntityType<?> entity, int i, String text) {
        self().add(entity + ".option" + i, text);
    }

    default void addDialogOption(RegistryObject<? extends EntityType<?>> entity, int i, String text) {
        self().add(entity.get() + ".option" + i, text);
    }

    default void addDialogOption(RegistryObject<? extends EntityType<?>> entity, String option, String text) {
        self().add(entity.get() + ".option." + option, text);
    }
}
