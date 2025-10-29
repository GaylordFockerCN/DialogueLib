package com.p1nero.dialog_lib.entity;

import com.p1nero.dialog_lib.DialogueLib;
import com.p1nero.dialog_lib.api.entity.custom.IEntityNpc;
import com.p1nero.dialog_lib.api.component.DialogNode;
import com.p1nero.dialog_lib.api.entity.goal.LookAtConservingPlayerGoal;
import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import com.p1nero.dialog_lib.client.screen.builder.DialogueScreenBuilder;
import com.p1nero.dialog_lib.client.screen.builder.StreamDialogueScreenBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ExampleEntity extends Mob implements IEntityNpc {

    protected ExampleEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * 在受伤时发起对话
     */
    @Override
    public boolean hurt(DamageSource damageSource, float value) {
        if(damageSource.getEntity() instanceof ServerPlayer serverPlayer) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putBoolean("from_hurt", true);
            sendDialogTo(serverPlayer, compoundTag);
        }
        return super.hurt(damageSource, value);
    }

    /**
     * 在右键时发起对话
     */
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(player instanceof ServerPlayer serverPlayer) {
            sendDialogTo(serverPlayer);
        }
        return super.mobInteract(player, hand);
    }

    /**
     * 对话或交易时看着玩家
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new LookAtConservingPlayerGoal<>(this));
    }

    /**
     * 翻译键对应 entity_id.mod_id.answer + 编号
     *  或 entity_id.mod_id.option + 编号
     * 编号对应 {@link com.p1nero.dialog_lib.datagen.ExampleLangProvider}
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public DialogueScreen getDialogueScreen(CompoundTag senderData) {
        StreamDialogueScreenBuilder builder = new StreamDialogueScreenBuilder(this, DialogueLib.MOD_ID);

        if(senderData.getBoolean("from_hurt")) {
            builder.start(0)
                    .addOption(0,1)
                    .thenExecute(1) //进入该对话节点后，发包给服务端处理
                    .thenExecute((dialogueScreen -> {
                        //进入该对话节点后，在客户端做些什么。比如更新当前窗口的图片展示
                        dialogueScreen.setPicture(ResourceLocation.parse("minecraft:test.png"));
                    }))
                    .addOption(1, 2);
            return builder.build();
        } else {
            DialogNode root = builder.newNode(3);

            DialogNode node1 = builder.newNode(3, 3)
                    .addExecutable(1)
                    .addExecutable((dialogueScreen -> {
                        doSomething();
                    }));

            DialogNode node2 = builder.newNode(4, 4);
            DialogNode node3 = builder.newNode(5, 5);
            node2.addChild(node3);//可以无限构造树

            root.addChild(node1)
                    .addChild(node2);
            return builder.buildWith(root);
        }
    }

    /**
     * 如果要终止对话，记得setConversingPlayer 为 null，否则会一直占用对话
     */
    @Override
    public void handleNpcInteraction(ServerPlayer player, int interactionID) {
        if (interactionID == 1) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.VILLAGER_CELEBRATE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if(interactionID == 2) {
            doSomething();
            this.setConversingPlayer(null);
        }

    }

    private void doSomething() {

    }

}
