package com.p1nero.dialog_lib.client.screen.component;

import com.p1nero.dialog_lib.DialogueLibConfig;
import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.MutableComponent;

/**
 * A button widget that allows the player to select a line of dialogue to say to an NPC.
 */
public class DialogueOptionComponent extends Button {

    private boolean broadcast;

    public DialogueOptionComponent(MutableComponent message, Button.OnPress onPress) {
        super(Button.builder(message, onPress).pos(0, 0).size(0, 12).createNarration(DEFAULT_NARRATION));
        this.width = Minecraft.getInstance().font.width(this.getMessage()) + 2;
        this.broadcast = false;
    }

    public DialogueOptionComponent(MutableComponent message, Button.OnPress onPress, boolean broadcast) {
        this(message, onPress);
        this.broadcast = broadcast;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(isHovered() || DialogueLibConfig.ENABLE_OPT_BACKGROUND.get()) {
            guiGraphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x66000000, 0x66000000);
        }
        guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 1, this.getY() + 1, 0xFFFFFF);
        if(isHovered()) {
            guiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, this.getMessage().getStyle().getColor() == null ? DialogueScreen.BORDER_COLOR : this.getMessage().getStyle().getColor().getValue());
        }
    }
}
