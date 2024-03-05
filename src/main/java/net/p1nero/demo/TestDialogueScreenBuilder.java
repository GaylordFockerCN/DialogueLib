package net.p1nero.demo;

import net.p1nero.dialogue_lib.screen.StreamDialogueScreenBuilder;
import net.p1nero.dialogue_lib.screen.TreeNode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class TestDialogueScreenBuilder extends StreamDialogueScreenBuilder {
    public TestDialogueScreenBuilder(Entity entity) {
        super(entity);
    }

    @Override
    protected void init() {

        answerRoot = new TreeNode(Component.literal("root根节点"))
                .addChild(new TreeNode(Component.literal("child子节点1"), Component.literal("option选项1")).execute(()->{})
                        .addLeaf(Component.literal("option选项1-1"), (byte) 0)
                        .addLeaf(Component.literal("option选项1-2"), (byte) 1)
                )
                .addChild(new TreeNode(Component.literal("child子节点2"), Component.literal("option选项2"))
                        .addChild(new TreeNode(Component.literal("child2's child 子节点2的子节点1"), Component.literal("option选项2-1"))
                                .addLeaf(Component.literal("option选项2-1-1"), (byte) 2)
                        )

                );

    }
}
