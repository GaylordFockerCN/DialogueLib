package com.p1nero.dialog_lib.demo.entity;

import com.p1nero.dialog_lib.api.NpcDialogueEntity;
import com.p1nero.dialog_lib.api.component.DialogueComponentBuilder;
import com.p1nero.dialog_lib.api.component.TreeNode;
import com.p1nero.dialog_lib.client.screen.DialogueScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DemoCustomEntity extends PathfinderMob implements NpcDialogueEntity {

    @Nullable
    private Player conversingPlayer;

    protected DemoCustomEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if(player instanceof ServerPlayer serverPlayer) {
            CompoundTag data = new CompoundTag();
            data.putBoolean("hello", true);
            this.sendDialogTo(serverPlayer, data);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public DialogueScreenBuilder getDialogueBuilder(CompoundTag senderData) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if(localPlayer == null) {
            return null;
        }
        DialogueScreenBuilder treeBuilder = new DialogueScreenBuilder(this);
        DialogueComponentBuilder dBuilder = new DialogueComponentBuilder(this);
        TreeNode root = new TreeNode(dBuilder.ans(0), dBuilder.opt(0));//开场白 | 返回

        TreeNode ans1 = new TreeNode(dBuilder.ans(1), dBuilder.opt(1))
                .addChild(root)
                .addLeaf(dBuilder.opt(2));

        TreeNode ans2 = new TreeNode(dBuilder.ans(2), dBuilder.opt(3))
                .addChild(root);

        TreeNode ans3;

        if(senderData.getBoolean("hello")) {
            ans3 = new TreeNode(dBuilder.ans(3), dBuilder.opt(4))
                    .addChild(root);
        } else {
            ans3 = new TreeNode(dBuilder.ans(4), dBuilder.opt(4))
                    .addLeaf(dBuilder.opt(5), 1);
        }

        root.addChild(ans1).addChild(ans2).addChild(ans3);

        treeBuilder.setAnswerRoot(root);
        return treeBuilder;
    }

    @Override
    public void handleNpcInteraction(ServerPlayer player, int interactionID) {
        if(interactionID == 1) {

        }
        this.setConversingPlayer(null);//NOTE!
    }

    @Override
    public void setConversingPlayer(@Nullable Player player) {
        this.conversingPlayer = player;
    }

    @Override
    public @Nullable Player getConversingPlayer() {
        return conversingPlayer;
    }
}
