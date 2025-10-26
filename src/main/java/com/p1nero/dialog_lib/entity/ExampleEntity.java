package com.p1nero.dialog_lib.entity;

import com.p1nero.dialog_lib.api.custom.IEntityNpc;
import com.p1nero.dialog_lib.api.component.DialogNode;
import com.p1nero.dialog_lib.api.goal.LookAtConservingPlayerGoal;
import com.p1nero.dialog_lib.client.screen.DialogueScreenBuilder;
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
import org.jetbrains.annotations.Nullable;

public class ExampleEntity extends Mob implements IEntityNpc {

    private Player conservingPlayer;

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

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new LookAtConservingPlayerGoal<>(this));//对话或交易时看着玩家
    }

    /**
     * 翻译键对应 entity_id.answer + 编号
     *  或 entity_id.option + 编号
     * 编号对应 {@link com.p1nero.dialog_lib.data.ExampleLangProvider}
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public DialogueScreenBuilder getDialogueBuilder(CompoundTag senderData) {
        DialogueScreenBuilder builder = new DialogueScreenBuilder(this);

        if(senderData.getBoolean("from_hurt")) {
            builder.start(0)
                    .addChoice(0,1)
                    .thenExecute(1) //进入该对话节点后，发包给服务端处理
                    .thenExecute((dialogueScreen -> {
                        //进入该对话节点后，在客户端做些什么。比如更新当前窗口的图片展示
                        dialogueScreen.setPicture(ResourceLocation.parse("minecraft:test.png"));
                    }))
                    .addChoice(1, 2);
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
        }

        return builder;
    }

    @Override
    public void handleNpcInteraction(ServerPlayer player, int interactionID) {
        if (interactionID == 1) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.VILLAGER_CELEBRATE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if(interactionID == 2) {
            doSomething();
            this.setConversingPlayer(null);//如果要终止对话，记得setConversingPlayer 为 null
        }

    }

    private void doSomething() {

    }

    @Override
    public void setConversingPlayer(@Nullable Player player) {
        this.conservingPlayer = player;
    }

    @Override
    public @Nullable Player getConversingPlayer() {
        return conservingPlayer;
    }
}
