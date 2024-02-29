package net.p1nero.dialogue_lib.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;

public class DialogueComponentBuilder {

    /**
     * 这个是用来构造一些对话内容用的，就是简单地加个[]，比如 [Boss]
     */

    public static MutableComponent buildTitle(Entity entity, ChatFormatting... nameChatFormatting){
        return Component.literal("[").append(entity.getDisplayName().copy().withStyle(nameChatFormatting)).append("]");
    }
    /**
     * 这个是用来构造一些对话内容用的，就是简单地加个[]，比如 [Boss]
     * 也可以自定义前后缀，比如 <Boss>
     */
    public static MutableComponent buildTitle(String pre,String suffix, Entity entity, ChatFormatting... nameChatFormatting){
        return Component.literal(pre).append(entity.getDisplayName().copy().withStyle(nameChatFormatting)).append(suffix);
    }

    /**
     * 这个是用来构造一些对话内容用的，就是简单地加个[]，比如 [Boss] hello！
     */
    public static MutableComponent buildDialogue(Entity entity, Component content ,ChatFormatting... nameChatFormatting){
        return Component.literal("[").append(entity.getDisplayName().copy().withStyle(nameChatFormatting)).append("]").append(content).withStyle();
    }
    /**
     * 这个是用来构造一些对话内容用的，就是简单地加个[]，比如 [Boss] hello！
     * 也可以自定义生物名称前后缀，比如 <Boss>: hello！
     */
    public static MutableComponent buildDialogue(String pre,String suffix,Entity entity, Component content ,ChatFormatting... nameChatFormatting){
        return Component.literal(pre).append(entity.getDisplayName().copy().withStyle(nameChatFormatting)).append(suffix).append(content).withStyle();
    }

}
