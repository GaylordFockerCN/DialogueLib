package com.p1nero.dialogue_lib.screen;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//为什么就没有现成的库呢因为太简单了吗
public class TreeNode {

    protected Component answer;
    protected Component option = Component.empty();
    @Nullable
    protected List<TreeNode> options;

    /**
     * 根节点不应该有选项。
    * */
    public TreeNode(Component answer) {
        this.answer = answer;
        this.options = new ArrayList<>();
    }

    public TreeNode(Component answer, Component option) {
        this.answer = answer;
        this.option = option;
        this.options = new ArrayList<>();
    }

    /**
     * 返回自己以方便构造
    * */
    public TreeNode addOption(Component answer, Component option) {
        options.add(new TreeNode(answer, option));
        return this;
    }
    public TreeNode addChild(TreeNode node) {
        options.add(node);
        return this;
    }

    public Component getAnswer() {
        return answer;
    }

    public Component getOption() {
        return option;
    }

    @Nullable
    public List<TreeNode> getChildren(){
        return options;
    }

    public static class FinalNode extends TreeNode{

        private byte returnValue;
        public FinalNode(Component finalOption, byte returnValue) {
            super(Component.empty());//最终节点不需要回答
            this.option = finalOption;
            this.returnValue = returnValue;
        }

        public byte getReturnValue(){
            return returnValue;
        }

    }

}

