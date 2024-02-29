package com.p1nero.demo;

import com.p1nero.dialogue_lib.screen.LinkListStreamDialogueScreenBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class TestLinkListStreamScreenBuilder extends LinkListStreamDialogueScreenBuilder {
    public TestLinkListStreamScreenBuilder(Entity entity, EntityType<?> entityType) {
        super(entity);
    }

    @Override
    public LinkListStreamDialogueScreenBuilder init() {
        return start(Component.literal("hello！"))
                .addChoice(Component.literal("你知道吗"),Component.literal("嗯？"))
                .addChoice(Component.literal("你真的不知道吗"),Component.literal("嗯哼？"))
                .addChoice(Component.literal("你真的真的知道吗"),Component.literal("嗯哼哼？"))
                .addChoice(Component.literal("你真的真的真的知道吗"),Component.literal("嗯哼哼哼？"));

    }
}
