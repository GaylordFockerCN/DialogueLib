package com.p1nero.dialog_lib.client.screen;

import com.p1nero.dialog_lib.DialogLibConfig;
import com.p1nero.dialog_lib.api.NpcDialogueEntity;
import com.p1nero.dialog_lib.client.screen.component.DialogueAnswerComponent;
import com.p1nero.dialog_lib.client.screen.component.DialogueChoiceComponent;
import com.p1nero.dialog_lib.network.PacketHandler;
import com.p1nero.dialog_lib.network.PacketRelay;
import com.p1nero.dialog_lib.network.packet.serverbound.NpcBlockPlayerInteractPacket;
import com.p1nero.dialog_lib.network.packet.serverbound.NpcPlayerInteractPacket;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

/**
 * 改编自theAether 的 ValkyrieQueenDialogueScreen
 * 搬运了相关类
 */
public class DialogueScreen extends Screen {
    protected ResourceLocation PICTURE_LOCATION = null;
    private int picHeight = 144, picWidth = 256;
    private int picShowHeight = 144, picShowWidth = 256;
    private int yOffset = 0;
    private boolean isSilent;
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
        typewriterInterval = DialogLibConfig.TYPEWRITER_EFFECT_INTERVAL.get();
        this.dialogueAnswer = new DialogueAnswerComponent(this.buildDialogueAnswerName(entity.getDisplayName().copy().withStyle(ChatFormatting.YELLOW)).append(": "));
        this.entity = entity;
        this.entityType = entity.getType();
    }

    public DialogueScreen(Component name, @Nullable Entity entity) {
        super(name);
        typewriterInterval = DialogLibConfig.TYPEWRITER_EFFECT_INTERVAL.get();
        this.dialogueAnswer = new DialogueAnswerComponent(name);
        this.entity = entity;
        if(entity != null) {
            this.entityType = entity.getType();
        }
    }

    public DialogueScreen(BlockEntity blockEntity) {
        super(blockEntity.getBlockState().getBlock().getName());
        typewriterInterval = DialogLibConfig.TYPEWRITER_EFFECT_INTERVAL.get();
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
        if(Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
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

    public void setupDialogueChoices(List<DialogueChoiceComponent> options) {
        this.clearWidgets();
        for (DialogueChoiceComponent option : options) {
            this.addRenderableWidget(option);
        }
        this.positionDialogue();
    }

    /**
     * 新增自动矫正位置，以及打字机效果
     * Repositions the dialogue answer and the player's dialogue choices based on the amount of choices.
     */
    protected void positionDialogue() {
        // Dialogue answer.
        this.dialogueAnswer.reposition(this.width, this.height * 5 / 4, yOffset);//相较于天堂的下移了一点，因为是中文
        // Dialogue choices.
        int lineNumber = this.dialogueAnswer.height / 12 + 1;
        Iterator<Renderable> iterator = this.renderables.iterator();
        while (iterator.hasNext()) {
            Renderable renderable = iterator.next();
            if (renderable instanceof DialogueChoiceComponent option) {
                option.setX(this.width / 2 - option.getWidth() / 2);
                int y = this.height / 2 * 5 / 4 + 12 * lineNumber + yOffset;
                option.setY(y);
                lineNumber++;
                int h = option.getHeight() + 2;
                if (!iterator.hasNext() && y + h > this.height && typewriterTimer < 0) {
                    yOffset -= h;
                    this.dialogueAnswer.reposition(this.width, this.height * 5 / 4, yOffset);
                    y = this.height / 2 * 5 / 4 + 12 * lineNumber + yOffset;
                    option.setY(y);//调低一点
                }
            }
        }
    }

    /**
     * Sets what message to display for a dialogue answer.
     * @param component The message {@link Component}.
     */
    protected void setDialogueAnswer(Component component) {
        if(DialogLibConfig.ENABLE_TYPEWRITER_EFFECT.get()){
            this.dialogueAnswer.updateTypewriterDialogue(component);
        }else {
            this.dialogueAnswer.updateDialogue(component);
        }

    }

    /**
     * Sets up the formatting for the Valkyrie Queen's key in the {@link DialogueAnswerComponent} widget.
     * @param component The key {@link Component}.
     * @return The formatted {@link MutableComponent}.
     */
    public MutableComponent buildDialogueAnswerName(Component component) {
        return Component.literal("[").append(component.copy().withStyle(ChatFormatting.YELLOW)).append("]");
    }

    /**
     * Sends an NPC interaction to the server, which is sent through a packet to be handled in {@link NpcDialogueEntity#handleNpcInteraction(ServerPlayer, byte)}.
     * @see NpcPlayerInteractPacket
     */
    protected void finishChat(int interactionID) {
        if(pos != null) {
            PacketRelay.sendToServer(PacketHandler.INSTANCE, new NpcBlockPlayerInteractPacket(pos, interactionID));
        }
        PacketRelay.sendToServer(PacketHandler.INSTANCE, new NpcPlayerInteractPacket(this.entity == null ? NpcPlayerInteractPacket.NO_ENTITY :this.entity.getId(), interactionID));
        PICTURE_LOCATION = null;
        yOffset = 0;
        picHeight = 144;
        picWidth = 256;
        picShowHeight = 256;
        picShowWidth = 144;
        super.onClose();
    }

    /**
     * 发包但不关闭窗口
     */
    protected void execute(int interactionID) {
        PacketRelay.sendToServer(PacketHandler.INSTANCE, new NpcPlayerInteractPacket(this.entity == null ? NpcPlayerInteractPacket.NO_ENTITY :this.entity.getId(), interactionID));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        this.renderPicture(guiGraphics);
        if(DialogLibConfig.ENABLE_TYPEWRITER_EFFECT.get() && typewriterTimer < 0) {
            this.dialogueAnswer.updateTypewriterDialogue();
            positionDialogue();
            typewriterTimer = typewriterInterval;
        } else {
            typewriterTimer--;
        }

        this.dialogueAnswer.render(guiGraphics);

        //如果回答还没显示完则不渲染选项
        for(Renderable renderable : this.renderables) {
            if(renderable instanceof DialogueChoiceComponent && !dialogueAnswer.shouldRenderOption()){
                continue;
            }
            renderable.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    private void renderPicture(GuiGraphics guiGraphics) {
        if(PICTURE_LOCATION != null){
            guiGraphics.blit(PICTURE_LOCATION, this.width/2 - picShowWidth/2, (int) (this.height/2 - picShowHeight / 1.3F), picShowWidth, picShowHeight, 0, 0, picWidth, picHeight, picWidth, picHeight);
        }
    }

    /**
     * [CODE COPY] - {@link Screen#renderBackground(GuiGraphics)}.<br><br>
     * Remove code for dark gradient and dirt background.
     */
    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
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