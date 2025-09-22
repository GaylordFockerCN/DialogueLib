package com.p1nero.dialog_lib.api.component;

import com.p1nero.dialog_lib.DialogueLib;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class DialogueComponentBuilder {

    public static final DialogueComponentBuilder BUILDER = new DialogueComponentBuilder();
    protected final EntityType<?> entityType;

    public DialogueComponentBuilder(@Nullable Entity entity) {
        this.entityType = entity == null ? null : entity.getType();
    }

    public DialogueComponentBuilder(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    public DialogueComponentBuilder() {
        this.entityType = null;
    }

    /**
     * 用于间隔发送一堆对话，用于演示npc之间的对话（有点简陋，具体时间看设备
     */
    public static void displayClientMessages(Player player, long interval, boolean actionBar, Runnable onDialogEnd, Component... messages) {
        new Thread(() -> {
            for (Component message : messages) {
                player.displayClientMessage(message, actionBar);
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    DialogueLib.LOGGER.error(e.getMessage());
                }
            }
            onDialogEnd.run();
        }).start();
    }

    public MutableComponent buildDialogue(Entity entity, Component content) {
        return Component.literal("[").append(entity.getDisplayName().copy().withStyle(ChatFormatting.YELLOW)).append("]:").append(content);
    }

    public MutableComponent buildDialogue(EntityType<?> entity, Component content) {
        return Component.literal("[").append(entity.getDescription().copy().withStyle(ChatFormatting.YELLOW)).append("]:").append(content);
    }

    public MutableComponent buildDialogue(Entity entity, Component content, ChatFormatting... nameChatFormatting) {
        return Component.literal("[").append(entity.getDisplayName().copy().withStyle(nameChatFormatting)).append("]:").append(content).withStyle();
    }

    public MutableComponent opt(EntityType<?> entityType, String key) {
        return Component.translatable(entityType + ".option." + key);
    }

    public MutableComponent optWithBrackets(EntityType<?> entityType, String key) {
        return Component.literal("[").append(opt(entityType, key)).append("]");
    }

    public MutableComponent opt(EntityType<?> entityType, int i) {
        return Component.translatable(entityType + ".option" + i);
    }

    public MutableComponent optWithBrackets(EntityType<?> entityType, int i) {
        return Component.literal("[").append(opt(entityType, i)).append("]");
    }

    public MutableComponent opt(String key) {
        return Component.translatable(entityType + ".option." + key);
    }

    public MutableComponent opt(String key, Object... params) {
        return Component.translatable(entityType + ".option" + key, params);
    }

    public MutableComponent opt(int i) {
        return Component.translatable(entityType + ".option" + i);
    }

    public MutableComponent optWithBrackets(int i) {
        return Component.literal("[").append(opt(i)).append("]");
    }

    public MutableComponent opt(int i, Object... params) {
        return Component.translatable(entityType + ".option" + i, params);
    }

    public MutableComponent optWithBrackets(int i, Object... params) {
        return Component.literal("[").append(opt(i, params)).append("]");
    }

    public MutableComponent ans(EntityType<?> entityType, int i, boolean newLine) {
        Component component = Component.translatable(entityType + ".answer" + i);

        return Component.literal(newLine ? "\n" : "").append(component);//换行符有效
    }

    public MutableComponent appendLine(String key, Object... objects) {
        Component component = Component.translatable(key, objects);
        return Component.literal("\n").append(component);
    }

    public MutableComponent ans(EntityType<?> entityType, int i, Object... objects) {
        Component component = Component.translatable(entityType + ".answer" + i, objects);
        return Component.literal("\n").append(component);//换行符有效
    }

    public MutableComponent ans(EntityType<?> entityType, int i, String s) {
        Component component = Component.translatable(entityType + ".answer" + i, s);
        return Component.literal("\n").append(component);
    }

    public MutableComponent ans(int i, boolean newLine) {
        Component component = Component.translatable(entityType + ".answer" + i);

        return Component.literal(newLine ? "\n" : "").append(component);//换行符有效
    }

    public MutableComponent ans(String s) {
        Component component = Component.translatable(entityType + ".answer" + s);
        return Component.literal("\n").append(component);//换行符有效
    }

    public MutableComponent ans(int i) {
        Component component = Component.translatable(entityType + ".answer" + i);
        return Component.literal("\n").append(component);//换行符有效
    }

    public MutableComponent ans(int i, Object... param) {
        Component component = Component.translatable(entityType + ".answer" + i, param);
        return Component.literal("\n").append(component);//换行符有效
    }

    public MutableComponent buildEntityAnswer(int i) {
        Component component = Component.translatable(entityType + ".answer" + i);
        return Component.literal("[").append(entityType.getDescription().copy().withStyle(ChatFormatting.YELLOW)).append(Component.literal("]: ").append(component));
    }

    public MutableComponent ans(int skinID, int i, boolean newLine) {
        Component component = Component.translatable(entityType + ".answer" + skinID + "_" + i);
        return Component.literal(newLine ? "\n" : "").append(component);//换行符有效
    }

    public MutableComponent ans(int i, String s) {
        Component component = Component.translatable(entityType + ".answer" + i, s);
        return Component.literal("\n").append(component);
    }

}
