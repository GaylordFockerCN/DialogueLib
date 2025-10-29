package com.p1nero.dialog_lib.api.datagen;

import com.p1nero.dialog_lib.mixin.LanguageProviderAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public interface DialogueLanguageProvider {
    default LanguageProvider self() {
        return (LanguageProvider)this;
    }

    default String modId() {
        return ((LanguageProviderAccessor)self()).getModId();
    }

    default void addDialogAnswer(EntityType<?> entity, int i, String text) {
        self().add(entity + "." + modId() + ".answer" + i, text);
    }

    default void addDialogAnswer(RegistryObject<? extends EntityType<?>> entity, int i, String text) {
        self().add(entity.get() + "." + modId() + ".answer" + i, text);
    }

    default void addDialogAnswer(RegistryObject<? extends EntityType<?>> entity, String answer, String text) {
        self().add(entity.get() + "." + modId() + ".answer" + answer, text);
    }

    default void addDialogOption(EntityType<?> entity, int i, String text) {
        self().add(entity + "." + modId() + ".option" + i, text);
    }

    default void addDialogOption(RegistryObject<? extends EntityType<?>> entity, int i, String text) {
        self().add(entity.get() + "." + modId() + ".option" + i, text);
    }

    default void addDialogOption(RegistryObject<? extends EntityType<?>> entity, String option, String text) {
        self().add(entity.get() + "." + modId() + ".option." + option, text);
    }

    default void addDialogAnswer(Block block, int i, String text) {
        self().add(block.getDescriptionId() + "." + modId() + ".answer" + i, text);
    }

    default void addDialogAnswer(Block block, String answer, String text) {
        self().add(block.getDescriptionId() + "." + modId() + ".answer" + answer, text);
    }

    default void addDialogOption(Block block, int i, String text) {
        self().add(block.getDescriptionId() + "." + modId() + ".option" + i, text);
    }

    default void addDialogOption(Block block, String option, String text) {
        self().add(block.getDescriptionId() + "." + modId() + ".option" + option, text);
    }

}
