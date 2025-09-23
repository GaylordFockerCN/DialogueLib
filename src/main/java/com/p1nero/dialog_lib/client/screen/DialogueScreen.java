package com.p1nero.dialog_lib.client.screen;

import com.p1nero.dialog_lib.DialogueLibConfig;
import com.p1nero.dialog_lib.client.screen.component.DialogueAnswerComponent;
import com.p1nero.dialog_lib.client.screen.component.DialogueOptionComponent;
import com.p1nero.dialog_lib.mixin.MobInvoker;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.serverbound.HandleNpcBlockPlayerInteractPacket;
import com.p1nero.dialog_lib.network.packet.serverbound.HandleNpcEntityPlayerInteractPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DialogueScreen extends Screen {
    protected ResourceLocation PICTURE_LOCATION = null;
    public static final int BACKGROUND_COLOR = 0xCC000000;
    public static final int BORDER_COLOR = 0xFFFFFFFF;
    private int picHeight = 144, picWidth = 256;
    private int picShowHeight = 144, picShowWidth = 256;
    private int yOffset = 0;
    private float rate;
    private boolean isSilent;
    private int currentOptionsCount;
    protected final DialogueAnswerComponent dialogueAnswer;
    @Nullable
    protected Entity entity;
    @Nullable
    protected BlockPos pos;
    public final int typewriterInterval;
    private int typewriterTimer = 0;
    @Nullable
    EntityType<?> entityType;

    public DialogueScreen(@NotNull Entity entity) {
        super(entity.getDisplayName());
        typewriterInterval = DialogueLibConfig.TYPEWRITER_EFFECT_INTERVAL.get();
        this.dialogueAnswer = new DialogueAnswerComponent(this.buildDialogueAnswerName(entity.getDisplayName().copy().withStyle(ChatFormatting.GOLD)).append(": "));
        this.entity = entity;
        this.entityType = entity.getType();
    }

    public DialogueScreen(Component name) {
        super(name);
        typewriterInterval = DialogueLibConfig.TYPEWRITER_EFFECT_INTERVAL.get();
        this.dialogueAnswer = new DialogueAnswerComponent(name);
    }

    public DialogueScreen(Component name, @NotNull Entity entity) {
        this(name);
        this.entity = entity;
        this.entityType = entity.getType();
    }

    public DialogueScreen(BlockEntity blockEntity) {
        this(blockEntity.getBlockState().getBlock().getName(), blockEntity);
    }

    public DialogueScreen(Component name, BlockEntity blockEntity) {
        super(name);
        typewriterInterval = DialogueLibConfig.TYPEWRITER_EFFECT_INTERVAL.get();
        this.dialogueAnswer = new DialogueAnswerComponent(blockEntity.getBlockState().getBlock().getName());
        this.pos = blockEntity.getBlockPos();
    }

    /**
     * 在这里实现对话逻辑调用
     */
    @Override
    protected void init() {
        positionDialogue();//不填的话用builder创造出来的对话框第一个对话会错误显示
    }

    public void setPicture(ResourceLocation resourceLocation) {
        this.PICTURE_LOCATION = resourceLocation;
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            Minecraft.getInstance().level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }
    }

    public void setSilent(boolean silent) {
        isSilent = silent;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setPicHeight(int picHeight) {
        this.picHeight = picHeight;
    }

    public void setPicWidth(int picWidth) {
        this.picWidth = picWidth;
    }

    public void setPicShowHeight(int picShowHeight) {
        this.picShowHeight = picShowHeight;
    }

    public void setPicShowWidth(int picShowWidth) {
        this.picShowWidth = picShowWidth;
    }

    protected void setupDialogueChoices(List<DialogueOptionComponent> options) {
        currentOptionsCount = options.size();
        this.clearWidgets();
        for (DialogueOptionComponent option : options) {
            this.addRenderableWidget(option);
        }
        this.positionDialogue();
    }

    /**
     * Repositions the dialogue answer and the player's dialogue choices based on the amount of choices.
     */
    protected void positionDialogue() {
        if (DialogueLibConfig.OPTION_IN_CENTER.get()) {
            positionDialogue1();
        } else {
            positionDialogue2();
        }
    }

    protected void positionDialogue1() {
        rate = 5.0F / 4;
        // Dialogue answer.
        this.dialogueAnswer.reposition(this.width, (int) (this.height * rate), yOffset);//相较于天堂的下移了一点，因为是中文
        // Dialogue choices.
        int lineNumber = this.dialogueAnswer.height / 12 + 1;
        Iterator<Renderable> iterator = this.renderables.iterator();
        while (iterator.hasNext()) {
            Renderable renderable = iterator.next();
            if (renderable instanceof DialogueOptionComponent option) {
                option.setX(this.width / 2 - option.getWidth() / 2);
                int y = (int) (this.height / 2.0 * rate + 12 * lineNumber + yOffset);
                option.setY(y);
                lineNumber++;
                int h = option.getHeight() + 2;
                if (!iterator.hasNext() && y + h > this.height && typewriterTimer < 0) {
                    yOffset -= h;
                    this.dialogueAnswer.reposition(this.width, (int) (this.height * rate), yOffset);
                    y = (int) (this.height / 2.0 * rate + 12 * lineNumber + yOffset);
                    option.setY(y);//调低一点
                }
            }
        }
    }
    protected void positionDialogue2() {
        // Dialogue answer.
        rate = 1.4F;
        this.dialogueAnswer.reposition(this.width, (int) (this.height * rate), yOffset);//相较于天堂的下移了一点，因为是中文
        int answerBottomY = (int) (this.height / 2.0 * rate + this.dialogueAnswer.height);
        if(this.height / 2.0 * rate + this.dialogueAnswer.height > this.height && typewriterTimer < 0){
            yOffset -= (answerBottomY - this.height);
        }
        // Dialogue choices.
        int lineNumber = 0;
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof DialogueOptionComponent option) {
                option.setX(this.width / 2 + this.width / 6);
                int y = (int) (this.height / 2.0 * rate + 12 * lineNumber + yOffset - currentOptionsCount * 12);
                option.setY(y);
                lineNumber++;
            }
        }
    }

    protected void setDialogueAnswer(Component component) {
        if (DialogueLibConfig.ENABLE_TYPEWRITER_EFFECT.get()) {
            this.dialogueAnswer.updateTypewriterDialogue(component);
        } else {
            this.dialogueAnswer.updateDialogue(component);
        }

    }

    public MutableComponent buildDialogueAnswerName(Component component) {
        return Component.literal("[").append(component.copy().withStyle(ChatFormatting.GOLD)).append("]");
    }

    protected void finishChat(int interactionID) {
        if (pos != null) {
            DialoguePacketRelay.sendToServer(DialoguePacketHandler.INSTANCE, new HandleNpcBlockPlayerInteractPacket(pos, interactionID));
        }
        DialoguePacketRelay.sendToServer(DialoguePacketHandler.INSTANCE, new HandleNpcEntityPlayerInteractPacket(this.entity == null ? HandleNpcEntityPlayerInteractPacket.NO_ENTITY : this.entity.getId(), interactionID));
        PICTURE_LOCATION = null;
        yOffset = 0;
        picHeight = 144;
        picWidth = 256;
        picShowHeight = 256;
        picShowWidth = 144;
        super.onClose();
    }

    /**
     * 默认对话翻页的时候播放声音
     */
    public void playSound() {
        if (this.isSilent || this.entity == null) {
            return;
        }
        if (this.entity instanceof Mob mob && ((MobInvoker) mob).dialog_lib$invokeGetAmbientSound() != null) {
            mob.level().playLocalSound(mob.getX(), mob.getY(), mob.getZ(), ((MobInvoker) mob).dialog_lib$invokeGetAmbientSound(), mob.getSoundSource(), 1.0F, 1.0F, false);
        }
    }

    /**
     * 发包但不关闭窗口
     */
    protected void execute(int interactionID) {
        DialoguePacketRelay.sendToServer(DialoguePacketHandler.INSTANCE, new HandleNpcEntityPlayerInteractPacket(this.entity == null ? HandleNpcEntityPlayerInteractPacket.NO_ENTITY : this.entity.getId(), interactionID));
    }

    /**
     * 单机后可提前显示
     */
    @Override
    public boolean mouseClicked(double v, double v1, int i) {
        if (this.dialogueAnswer.index < dialogueAnswer.max - 3) {
            this.dialogueAnswer.index = dialogueAnswer.max - 3;
        }
        return super.mouseClicked(v, v1, i);
    }

    /**
     * 回答是否全部可见
     */
    protected boolean shouldRenderOption(){
        if(DialogueLibConfig.ENABLE_TYPEWRITER_EFFECT.get()) {
            return this.dialogueAnswer.shouldRenderOption();
        }
        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        this.renderPicture(guiGraphics);
        if (DialogueLibConfig.ENABLE_TYPEWRITER_EFFECT.get() && typewriterTimer < 0) {
            this.dialogueAnswer.updateTypewriterDialogue();
            positionDialogue();
            typewriterTimer = typewriterInterval;
        } else {
            typewriterTimer--;
        }

        this.dialogueAnswer.render(guiGraphics);

        //如果回答还没显示完则不渲染选项
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof DialogueOptionComponent && !dialogueAnswer.shouldRenderOption()) {
                continue;
            }
            renderable.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    protected void renderPicture(GuiGraphics guiGraphics) {
        if (PICTURE_LOCATION != null) {
            guiGraphics.blit(PICTURE_LOCATION, this.width / 2 - picShowWidth / 2, (int) ((float) this.height / 2 - picShowHeight / 1.3F), picShowWidth, picShowHeight, 0, 0, picWidth, picHeight, picWidth, picHeight);
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
        if(DialogueLibConfig.ENABLE_BACKGROUND.get()) {
            int tooltipHeight = dialogueAnswer.height + 10;
            if(DialogueLibConfig.OPTION_IN_CENTER.get()) {
                tooltipHeight += (currentOptionsCount + 1) * 12;
            }
            int posY = (int) (this.height / 2.0 * rate + yOffset - 5);
            int tooltipWidth = 340;
            int posX = this.width / 2 - tooltipWidth / 2;

            if(DialogueLibConfig.FADED_BACKGROUND.get()) {
                int gradientHeight = this.height - posY;
                for (int i = 0; i < gradientHeight; i++) {
                    float progress = (float) i / gradientHeight;
                    float curve = progress * progress;
                    int alpha = (int) (0xA0 * (1 - curve));
                    int color = (alpha << 24);
                    if (alpha > 0) {
                        int currentY = this.height - i;
                        guiGraphics.fill(0, currentY, this.width, currentY + 1, color);
                    }
                }

            } else {
                guiGraphics.fill(posX, posY, posX + tooltipWidth, posY + tooltipHeight, BACKGROUND_COLOR);
                guiGraphics.renderOutline(posX, posY, tooltipWidth, tooltipHeight, BORDER_COLOR);
            }
        }
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;
        this.positionDialogue();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.finishChat(0);
    }

}