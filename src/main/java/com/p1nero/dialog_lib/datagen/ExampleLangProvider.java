package com.p1nero.dialog_lib.datagen;

import com.p1nero.dialog_lib.api.datagen.DialogueLanguageProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.LanguageProvider;

public class ExampleLangProvider extends LanguageProvider implements DialogueLanguageProvider {
    public ExampleLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    /**
     * 编号和实体类型对应 {@link com.p1nero.dialog_lib.entity.ExampleEntity}
     */
    @Override
    protected void addTranslations() {
        this.addDialogAnswer(EntityType.DONKEY, 0, "Hello");
        this.addDialogAnswer(EntityType.DONKEY, 1, "World!");
        this.addDialogAnswer(EntityType.DONKEY, 2, "Test");

        this.addDialogOption(EntityType.DONKEY, 0, "Ok");
        this.addDialogOption(EntityType.DONKEY, 1, "Yes");
        this.addDialogOption(EntityType.DONKEY, 2, "Right");
    }
}
