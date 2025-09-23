package com.p1nero.dialog_lib.client.screen;

import com.p1nero.dialog_lib.api.IEntityNpc;
import com.p1nero.dialog_lib.api.component.DialogueComponentBuilder;
import com.p1nero.dialog_lib.api.component.DialogNode;
import com.p1nero.dialog_lib.client.screen.component.DialogueOptionComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 如果要链式对话就用start和addChoice
 * 如果要构建复杂对话就手动设置root
 */
public class DialogueScreenBuilder {

    protected DialogueScreen screen;//封装一下防止出现一堆杂七杂八的方法
    protected DialogueComponentBuilder builder;
    protected DialogNode root;
    protected DialogNode tempNode;
    @Nullable
    protected EntityType<?> entityType;

    public DialogueScreenBuilder(Component name) {
        this.screen = new DialogueScreen(name);
    }

    public DialogueScreenBuilder(Entity entity) {
        this.screen = new DialogueScreen(entity);
        this.entityType = entity.getType();
        this.builder = new DialogueComponentBuilder(entity);
        init();
    }

    public DialogueScreenBuilder(Entity entity, Component name) {
        this.screen = new DialogueScreen(name, entity);
        this.entityType = entity.getType();
        this.builder = new DialogueComponentBuilder(entity);
        init();
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
        builder = new DialogueComponentBuilder(entityType);
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void setRoot(DialogNode root) {
        this.root = root;
    }

    public void setScreen(DialogueScreen screen) {
        this.screen = screen;
    }

    /**
     * 重写这个是为了让你记得这才是Screen真正被调用的初始化的地方。建议在这里作些判断再调用start。
     */
    public DialogueScreenBuilder init() {
        return this;
    }

    /**
     * 初始化对话框，得先start才能做后面的操作
     *
     * @param greeting 初始时显示的话
     */
    public DialogueScreenBuilder start(Component greeting, int executeValue, Consumer<DialogueScreen> screenConsumer) {
        root = new DialogNode(greeting, Component.empty(), executeValue, screenConsumer);
        tempNode = root;
        return this;
    }

    public DialogueScreenBuilder start(Component greeting, Consumer<DialogueScreen> screenConsumer) {
        return start(greeting, DialogNode.NOT_EXECUTE, screenConsumer);
    }

    public DialogueScreenBuilder start(Component greeting, int executeValue) {
        return start(greeting, executeValue, null);
    }

    public DialogueScreenBuilder start(Component greeting) {
        root = new DialogNode(greeting);
        tempNode = root;
        return this;
    }

    /**
     * 初始化对话框，得先start才能做后面的操作
     *
     * @param greeting 初始时显示的话的编号
     */
    public DialogueScreenBuilder start(int greeting) {
        return start(builder.ans(greeting));
    }

    public DialogueScreenBuilder start(int greeting, int executeValue) {
        return start(builder.ans(greeting), executeValue);
    }

    public DialogueScreenBuilder start(int greeting, Consumer<DialogueScreen> screenConsumer) {
        return start(builder.ans(greeting), screenConsumer);
    }

    public DialogueScreenBuilder start(int greeting, int executeValue, Consumer<DialogueScreen> screenConsumer) {
        return start(builder.ans(greeting), executeValue, screenConsumer);
    }

    /**
     * @param finalOption 最后显示的话
     * @param returnValue 选项的返回值，默认返回0。用于处理 {@link IEntityNpc#handleNpcInteraction(ServerPlayer, int)}
     */
    public DialogueScreenBuilder addFinalChoice(Component finalOption, int returnValue) {
        return addFinalChoice(finalOption, returnValue, null);
    }

    public DialogueScreenBuilder addFinalChoice(Component finalOption, int returnValue, Consumer<DialogueScreen> screenConsumer) {
        if (tempNode == null)
            return null;
        tempNode.addChild(new DialogNode.FinalNode(finalOption, returnValue, screenConsumer));
        return this;
    }

    public DialogueScreenBuilder addFinalChoice(Component finalOption, Consumer<DialogueScreen> screenConsumer) {
        if (tempNode == null)
            return null;
        tempNode.addChild(new DialogNode.FinalNode(finalOption, 0, screenConsumer));
        return this;
    }

    public DialogueScreenBuilder addFinalChoice(Component finalOption) {
        return addFinalChoice(finalOption, 0);
    }

    /**
     * @param finalOption 最后显示的话
     * @param returnValue 选项的返回值，默认返回0。用于处理 {@link IEntityNpc#handleNpcInteraction(ServerPlayer, int)}
     */
    public DialogueScreenBuilder addFinalChoice(int finalOption, int returnValue) {
        return addFinalChoice(builder.opt(finalOption), returnValue);
    }

    public DialogueScreenBuilder addFinalChoice(int finalOption, int returnValue, Consumer<DialogueScreen> screenConsumer) {
        return addFinalChoice(builder.opt(finalOption), returnValue, screenConsumer);
    }

    public DialogueScreenBuilder addFinalChoice(int finalOption, Consumer<DialogueScreen> screenConsumer) {
        return addFinalChoice(builder.opt(finalOption), screenConsumer);
    }

    public DialogueScreenBuilder addFinalChoice(int finalOption) {
        return addFinalChoice(finalOption, 0);
    }

    /**
     * 添加选项进树并返回下一个节点
     *
     * @param option 该选项的内容
     * @param answer 选择该选项后的回答内容
     */
    public DialogueScreenBuilder addChoice(Component option, Component answer, int executeValue, Consumer<DialogueScreen> screenConsumer) {
        if (tempNode == null)
            return null;
        tempNode.addChild(answer, option, executeValue, screenConsumer);

        //直接下一个
        List<DialogNode> list = tempNode.getChildren();
        if (!(list.size() == 1 && list.get(0) instanceof DialogNode.FinalNode)) {
            tempNode = list.get(0);
        }

        return this;
    }

    public DialogueScreenBuilder addChoice(Component option, Component answer) {
        return addChoice(option, answer, DialogNode.NOT_EXECUTE, null);
    }

    public DialogueScreenBuilder addChoice(Component option, Component answer, Consumer<DialogueScreen> screenConsumer) {
        return addChoice(option, answer, DialogNode.NOT_EXECUTE, screenConsumer);
    }

    public DialogueScreenBuilder addChoice(Component option, Component answer, int executeValue) {
        return addChoice(option, answer, executeValue, null);
    }

        /**
         * 添加选项进树并返回当前节点
         *
         * @param option 该选项的内容
         * @param answer 选择该选项后的回答内容
         */
    public DialogueScreenBuilder addChoiceAndStayCurrent(Component option, Component answer) {
        if (tempNode == null)
            return null;
        tempNode.addChild(answer, option);
        return this;
    }

    /**
     * 使用BUILDER构建
     * 添加选项进树并返回下一个节点
     *
     * @param option 该选项的内容编号
     * @param answer 选择该选项后的回答内容编号
     */
    public DialogueScreenBuilder addChoice(int option, int answer, int executeValue, Consumer<DialogueScreen> screenConsumer) {
        return addChoice(DialogueComponentBuilder.BUILDER.opt(entityType, option), DialogueComponentBuilder.BUILDER.ans(entityType, answer), executeValue, screenConsumer);
    }

    public DialogueScreenBuilder addChoice(int option, int answer, Consumer<DialogueScreen> screenConsumer) {
        return addChoice(option, answer, DialogNode.NOT_EXECUTE, screenConsumer);
    }

    public DialogueScreenBuilder addChoice(int option, int answer, int executeValue) {
        return addChoice(option, answer, executeValue, null);
    }

    public DialogueScreenBuilder addChoice(int option, int answer) {
        return addChoice(option, answer, DialogNode.NOT_EXECUTE, null);
    }

    /**
     * 按下按钮后执行
     */
    public DialogueScreenBuilder thenExecute(Consumer<DialogueScreen> consumer) {
        if (tempNode == null)
            return null;
        tempNode.addExecutable(consumer);
        return this;
    }

    /**
     * 按下按钮后执行。记得在handle的时候不要把玩家设置为null，提前返回，否则可能中断对话！
     */
    public DialogueScreenBuilder thenExecute(int returnValue) {
        tempNode.addExecutable(returnValue);
        return this;
    }


    public DialogNode newNode(int ans) {
        return new DialogNode(builder.ans(ans), Component.empty(), DialogNode.NOT_EXECUTE, null);
    }

    public DialogNode newNode(int ans, int opt) {
        return new DialogNode(builder.ans(ans), builder.opt(opt), DialogNode.NOT_EXECUTE, null);
    }

    public DialogNode newNode(int ans, int opt, Consumer<DialogueScreen> consumer) {
        return new DialogNode(builder.ans(ans), builder.opt(opt), DialogNode.NOT_EXECUTE, consumer);
    }

    public DialogNode newNode(int ans, int opt, int execute) {
        return new DialogNode(builder.ans(ans), builder.opt(opt), execute, null);
    }

    public DialogNode newNode(int ans, int opt, int execute, Consumer<DialogueScreen> consumer) {
        return new DialogNode(builder.ans(ans), builder.opt(opt), execute, consumer);
    }

    public DialogNode newFinalNode(int opt) {
        return newFinalNode(opt, 0, null);
    }

    public DialogNode newFinalNode(int opt, int returnValue) {
        return newFinalNode(opt, returnValue, null);
    }

    public DialogNode newFinalNode(int opt, int returnValue, Consumer<DialogueScreen> consumer) {
        return new DialogNode.FinalNode(builder.opt(opt), returnValue, consumer);
    }

    public DialogueComponentBuilder getComponentBuildr() {
        return builder;
    }

    /**
     * 用于构建复杂的对话
     */
    public DialogueScreen buildWith(DialogNode customRoot) {
        this.setRoot(customRoot);
        return build();
    }

    /**
     * 根据树来建立套娃按钮
     */
    public DialogueScreen build() {
        if (root == null)
            return screen;
        screen.setDialogueAnswer(root.getAnswer());
        List<DialogueOptionComponent> choiceList = new ArrayList<>();
        for (DialogNode child : root.getChildren()) {
            if(child.getOption() != null) {
                choiceList.add(new DialogueOptionComponent(child.getOption().copy(), createChoiceButton(child)));
            }
        }
        screen.setupDialogueChoices(choiceList);
        return screen;
    }

    /**
     * 递归添加按钮。放心如果遇到没有添加选项的节点会自动帮你添加一个返回空内容返回值为0的FinalNode。
     */
    private Button.OnPress createChoiceButton(DialogNode node) {

        //如果是终止按钮则实现返回效果
        if (node instanceof DialogNode.FinalNode finalNode) {
            return button -> {
                if(!screen.shouldRenderOption()) {
                    return;
                }
                //先发包后execute，防止有setScreen之类的被顶掉
                screen.finishChat(finalNode.getReturnValue());
                if (finalNode.canExecute()) {
                    finalNode.execute(screen);
                }
            };
        }

        //否则继续递归创建按钮
        return button -> {
            if(!screen.shouldRenderOption()) {
                return;
            }
            if (node.canExecute()) {
                node.execute(screen);
            }
            if (node.canExecuteCode()) {
                if (node.getExecuteValue() == 0) {
                    throw new IllegalArgumentException("The return value '0' is used by default. and this will cause conservation stop!");
                }
                screen.execute(node.getExecuteValue());
            }
            screen.setDialogueAnswer(node.getAnswer());
            List<DialogueOptionComponent> choiceList = new ArrayList<>();
            List<DialogNode> options = node.getChildren();
            if (options == null) {
                options = new ArrayList<>();
                options.add(new DialogNode.FinalNode(Component.empty(), 0));
            }
            for (DialogNode child : options) {
                choiceList.add(new DialogueOptionComponent(child.getOption().copy(), createChoiceButton(child)));
            }
            screen.setupDialogueChoices(choiceList);
        };
    }

}
