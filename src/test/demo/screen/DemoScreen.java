package com.p1nero.dialog_lib.demo.screen;

import com.p1nero.dialog_lib.api.component.TreeNode;
import com.p1nero.dialog_lib.client.screen.DialogueScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DemoScreen {
    @OnlyIn(Dist.CLIENT)
    public static void addScreen() {
        DialogueScreenBuilder screenBuilder = new DialogueScreenBuilder(null, Component.literal("Screen Title").append(": \n"));

        TreeNode root = new TreeNode(Component.literal("Hello! this is a test screen!"))
                .addLeaf(Component.literal("Good bye!"));

        screenBuilder.setAnswerRoot(root);
        Minecraft.getInstance().setScreen(screenBuilder.build());
    }

}
