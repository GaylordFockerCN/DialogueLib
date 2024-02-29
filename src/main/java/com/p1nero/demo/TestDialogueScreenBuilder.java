package com.p1nero.demo;

import com.p1nero.dialogue_lib.screen.StreamDialogueScreenBuilder;
import com.p1nero.dialogue_lib.screen.TreeNode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class TestDialogueScreenBuilder extends StreamDialogueScreenBuilder {
    public TestDialogueScreenBuilder(Entity entity) {
        super(entity);
    }

    @Override
    public StreamDialogueScreenBuilder init() {

        answerRoot = new TreeNode(Component.literal("根节点"))
                .addChild(new TreeNode(Component.literal("子节点1"), Component.literal("选项1"))
                        .addOption(Component.literal("子节点1的子节点1"), Component.literal("选项1-1"))
                        .addOption(Component.literal("子节点1的子节点2"), Component.literal("选项1-2"))
                )
                .addChild(new TreeNode(Component.literal("子节点2"), Component.literal("选项2"))
                        .addChild(new TreeNode(Component.literal("子节点2的子节点1"), Component.literal("选项2-1"))
                                .addOption(Component.literal("子节点2的子节点1的子节点1"), Component.literal("选项2-1-1"))
                        )
                );

        return this;
    }
}
