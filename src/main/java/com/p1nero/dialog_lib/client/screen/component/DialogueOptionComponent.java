package com.p1nero.dialog_lib.client.screen.component;

import com.p1nero.dialog_lib.DialogueLibConfig;
import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.MutableComponent;

public class DialogueOptionComponent extends Button {

    public DialogueOptionComponent(MutableComponent message, Button.OnPress onPress) {
        super(Button.builder(message, onPress).pos(0, 0).size(0, 12).createNarration(DEFAULT_NARRATION));
        this.width = Minecraft.getInstance().font.width(this.getMessage()) + 2;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(isHovered() || isFocused() || DialogueLibConfig.ENABLE_OPT_BACKGROUND.get()) {
            guiGraphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x66000000, 0x66000000);
        }
        guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 1, this.getY() + 1, 0xFFFFFF);
        if(isHovered() || isFocused()) {
            guiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, this.getMessage().getStyle().getColor() == null ? DialogueScreen.BORDER_COLOR : this.getMessage().getStyle().getColor().getValue());
        }
    }
}
