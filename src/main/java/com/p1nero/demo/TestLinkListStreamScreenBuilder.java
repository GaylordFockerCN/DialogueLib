package com.p1nero.demo;

import com.p1nero.dialogue_lib.screen.LinkListStreamDialogueScreenBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import static com.p1nero.dialogue_lib.DialogueLib.SAMPLE;

public class TestLinkListStreamScreenBuilder extends LinkListStreamDialogueScreenBuilder {
    public TestLinkListStreamScreenBuilder(Entity entity) {
        super(entity);
    }

    @Override
    protected void init() {
        this.start(Component.literal("hello！"))
            .addChoice(Component.literal("You know你知道吗"),Component.literal("Huh嗯？"))
            .addChoice(Component.literal("你真的不知道吗"),Component.literal("嗯哼？"))
            .addChoice(Component.literal("你真的真的知道吗"),Component.literal("嗯哼哼？"))
            .addFinalChoice(Component.literal("Bye"),(byte)3);
    }

    //建议使用一些辅助函数来构建带翻译文本的Component
    //Suggest using some auxiliary functions to build a Component with translated text
    public Component buildChoiceComponent(int i){
        return Component.translatable(SAMPLE.get()+".choice."+i);
    }


}
