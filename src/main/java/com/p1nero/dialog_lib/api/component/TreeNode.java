package com.p1nero.dialog_lib.api.component;

import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TreeNode {

    protected Component answer;
    protected Component option = Component.empty();

    @Nullable
    protected Consumer<DialogueScreen> screenConsumer;//要执行的操作

    public int getExecuteValue() {
        return executeValue;
    }

    protected int executeValue = -114514;//要执行的操作代码 ，114514 代表无操作

    protected List<TreeNode> options = new ArrayList<>();

    /**
     * 根节点不应该有选项。
     *
     */
    public TreeNode(Component answer) {
        this.answer = answer;
    }

    public TreeNode(Component answer, Component option) {
        this.answer = answer;
        this.option = option;
    }

    public TreeNode addLeaf(Component option, int returnValue) {
        options.add(new FinalNode(option, returnValue));
        return this;
    }

    /**
     * 默认的情况。负数不会被处理
     */
    public TreeNode addLeaf(Component option) {
        options.add(new FinalNode(option, -1));
        return this;
    }

    public TreeNode addChild(Component answer, Component option) {
        options.add(new TreeNode(answer, option));
        return this;
    }

    public TreeNode addChild(TreeNode node) {
        options.add(node);
        return this;
    }

    public TreeNode foreachAdd(TreeNode node) {
        options.forEach(treeNode -> treeNode.addChild(node));
        return this;
    }

    public TreeNode addExecutable(Consumer<DialogueScreen> runnable) {
        this.screenConsumer = runnable;
        return this;
    }

    public TreeNode addExecutable(int executeValue) {
        this.executeValue = executeValue;
        return this;
    }

    public void execute(DialogueScreen screen) {
        if (screenConsumer != null) {
            screenConsumer.accept(screen);
        }
    }

    public boolean canExecute() {
        return screenConsumer != null;
    }

    public boolean canExecuteCode() {
        return executeValue != -114514;
    }

    public Component getAnswer() {
        return answer;
    }

    public Component getOption() {
        return option;
    }

    public List<TreeNode> getChildren() {
        return options;
    }

    public static class FinalNode extends TreeNode {
        private final int returnValue;

        public FinalNode(Component finalOption, int returnValue) {
            super(Component.empty());//最终节点不需要回答
            this.option = finalOption;
            this.returnValue = returnValue;
        }

        public FinalNode(Component finalOption, int returnValue, Consumer<DialogueScreen> consumer) {
            this(finalOption, returnValue);
            this.screenConsumer = consumer;
        }

        public int getReturnValue() {
            return returnValue;
        }

    }

}

