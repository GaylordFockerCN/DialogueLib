package net.p1nero.demo;

import net.p1nero.dialogue_lib.entity.Dialogueable;
import net.p1nero.dialogue_lib.entity.ai.goal.NpcDialogueGoal;
import net.p1nero.dialogue_lib.network.PacketHandler;
import net.p1nero.dialogue_lib.network.PacketRelay;
import net.p1nero.dialogue_lib.network.packet.NPCDialoguePacket;
import net.p1nero.dialogue_lib.screen.DialogueComponentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RhinoEntity extends Mob implements Dialogueable {

    private Player conversingPlayer;
    public RhinoEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new NpcDialogueGoal<>(this));

    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            if (!this.level().isClientSide()) {
                this.lookAt(player, 180.0F, 180.0F);
                    if (this.getConversingPlayer() == null) {
                        PacketRelay.sendToPlayer(PacketHandler.INSTANCE, new NPCDialoguePacket(this.getId()), ((ServerPlayer) player));
                        this.setConversingPlayer(player);
                    }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)//最大血量
                .add(Attributes.ATTACK_DAMAGE, 0)//单次攻击伤害
                .add(Attributes.ATTACK_SPEED, 0)//攻速
                .add(Attributes.MOVEMENT_SPEED, 0)//移速
                .build();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HOGLIN_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.DOLPHIN_DEATH;
    }

    @Override
    public void openDialogueScreen() {

        if(new Random().nextBoolean()){
            Minecraft.getInstance().setScreen(new TestDialogueScreenBuilder(this).build());
        }else {
            Minecraft.getInstance().setScreen(new TestLinkListStreamScreenBuilder(this).build());
        }

    }

    @Override
    public void handleNpcInteraction(Player player, byte interactionID) {
        switch (interactionID) {
            case 0:
                this.chat(Component.literal("return 0"));
                break;
            case 1:
                this.chat(Component.literal("return 1"));
                break;
            case 2:
                this.chat(Component.literal("return 2"));
                break;
            case 3:
                this.chat(Component.literal("Bye~"));
                break;
        }
        this.setConversingPlayer(null);
    }

    public void chat(Component component) {
        if(conversingPlayer != null){
            conversingPlayer.sendSystemMessage(DialogueComponentBuilder.buildDialogue(this, component));
        }
    }

    @Override
    public void setConversingPlayer(@Nullable Player player) {
        conversingPlayer = player;
    }

    @Nullable
    @Override
    public Player getConversingPlayer() {
        return conversingPlayer;
    }

}