package net.p1nero.dialogue_lib.screen;

import net.p1nero.dialogue_lib.DialogueLib;
import net.p1nero.dialogue_lib.entity.Dialogueable;
import net.p1nero.dialogue_lib.network.PacketHandler;
import net.p1nero.dialogue_lib.network.PacketRelay;
import net.p1nero.dialogue_lib.network.packet.NpcPlayerInteractPacket;
import net.p1nero.dialogue_lib.screen.component.DialogueAnswerComponent;
import net.p1nero.dialogue_lib.screen.component.DialogueChoiceComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.p1nero.dialogue_lib.util.DialogueComponentBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 改编自theAether 的 ValkyrieQueenDialogueScreen
 * 并搬运了相关类
 */
public class DialogueScreen extends Screen {
    public static final ResourceLocation MY_BACKGROUND_LOCATION = new ResourceLocation(DialogueLib.MOD_ID,"textures/gui/background.png");
    protected final DialogueAnswerComponent dialogueAnswer;

    @Nullable
    protected final Entity entity;//用于做一些判断

    public DialogueScreen(Entity entity) {
        super(entity.getDisplayName());
        this.dialogueAnswer = new DialogueAnswerComponent(DialogueComponentBuilder.buildTitle(entity,ChatFormatting.YELLOW));
        this.entity = entity;
    }

    /**
     * 在这里实现对话逻辑调用
     */
    @Override
    protected void init() {
        positionDialogue();//不填的话用builder创造出来的对话框第一个对话会错误显示
    }

    /**
     * Adds and repositions a new set of dialogue options.
     * @param options The {@link DialogueChoiceComponent} option buttons to render.
     */
    public void setupDialogueChoices(DialogueChoiceComponent... options) {
        this.clearWidgets();
        for (DialogueChoiceComponent option : options) {
            this.addRenderableWidget(option);
        }
        this.positionDialogue();
    }

    public void setupDialogueChoices(List<DialogueChoiceComponent> options) {
        this.clearWidgets();
        for (DialogueChoiceComponent option : options) {
            this.addRenderableWidget(option);
        }
        this.positionDialogue();
    }

    /**
     * Repositions the Valkyrie Queen's dialogue answer and the player's dialogue choices based on the amount of choices.
     */
    protected void positionDialogue() {
        // Dialogue answer.
        this.dialogueAnswer.reposition(this.width, this.height *5/4);//相较于天堂的下移了一点
        // Dialogue choices.
        int lineNumber = this.dialogueAnswer.height / 12 + 1;
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof DialogueChoiceComponent option) {
                option.setX(this.width / 2 - option.getWidth() / 2);
                option.setY(this.height / 2 *5/4 + 12 * lineNumber);//调低一点
                lineNumber++;
            }
        }
    }

    /**
     * Sets what message to display for a dialogue answer.
     * @param component The message {@link Component}.
     */
    protected void setDialogueAnswer(Component component) {
        this.dialogueAnswer.updateDialogue(component);
    }

    /**
     * Sends an NPC interaction to the server, which is sent through a packet to be handled in {@link Dialogueable#handleNpcInteraction(Player, byte)}.
     * @param interactionID A code for which interaction was performed on the client.<br>
     * @see NpcPlayerInteractPacket
     */
    protected void finishChat(byte interactionID) {
        PacketRelay.sendToServer(PacketHandler.INSTANCE, new NpcPlayerInteractPacket(this.entity.getId(), interactionID));
        super.onClose();
    }

    /**
     * Sends an NPC interaction to the server, which is sent through a packet to be handled in {@link Dialogueable#handleNpcInteraction(Player, byte)}.
     * 但是不关闭窗口。
     * NOTE 在handle的时候不要将玩家设为空！！
     * @param interactionID A code for which interaction was performed on the client.<br>
     * @see NpcPlayerInteractPacket
     */
    protected void execute(byte interactionID) {
        PacketRelay.sendToServer(PacketHandler.INSTANCE, new NpcPlayerInteractPacket(this.entity.getId(), interactionID));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        this.dialogueAnswer.render(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    /**
     * [CODE COPY] - {@link Screen#renderBackground(GuiGraphics)}.<br><br>
     * Remove code for dark gradient and dirt background.
     */
    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        if (this.getMinecraft().level != null) {
            MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(this, guiGraphics));
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
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

    }

}