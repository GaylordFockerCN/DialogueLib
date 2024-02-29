package net.p1nero.dialogue_lib.screen;

import net.p1nero.dialogue_lib.screen.component.DialogueChoiceComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class StreamDialogueScreenBuilder {

    protected Entity entity;

    protected DialogueScreen screen;//封装一下防止出现一堆杂七杂八的方法，但是似乎阻止了ECS关窗口（onClose失效）
    protected TreeNode answerRoot;

    public StreamDialogueScreenBuilder(Entity entity) {
        screen = new DialogueScreen(entity);
        this.entity = entity;
        init();
    }

    /**
     * 重写这个是为了让你记得这才是Screen真正被调用的初始化的地方。建议在这里作些判断再调用start。
     * */
    protected void init(){
    }

    /**
     * 根据树来建立套娃按钮
     */
    public DialogueScreen build(){
        if(answerRoot == null)
            return screen;
        screen.setDialogueAnswer(answerRoot.getAnswer());
        List<DialogueChoiceComponent> choiceList = new ArrayList<>();
        for(TreeNode child : answerRoot.getChildren()){
            choiceList.add(new DialogueChoiceComponent(child.getOption().copy(), createChoiceButton(child)));
        }
        screen.setupDialogueChoices(choiceList);
        return screen;
    }

    /**
     * 递归添加按钮。放心如果遇到没有添加选项的节点会自动帮你添加一个返回空内容返回值为0的FinalNode。
     */
    private Button.OnPress createChoiceButton(TreeNode node){

        //如果是终止按钮则实现返回效果
        if(node instanceof TreeNode.FinalNode finalNode){
            return button -> {screen.finishChat(finalNode.getReturnValue());};
        }

        //否则继续递归创建按钮
        return button -> {
            screen.setDialogueAnswer(node.getAnswer());
            List<DialogueChoiceComponent> choiceList = new ArrayList<>();
            List<TreeNode> options = node.getChildren();
            if(options == null){
                options = new ArrayList<>();
                options.add(new TreeNode.FinalNode(Component.empty(),(byte) 0));
            }
            for(TreeNode child : options){
                choiceList.add(new DialogueChoiceComponent(child.getOption().copy(), createChoiceButton(child)));
            }
            screen.setupDialogueChoices(choiceList);
        };
    }

    public void onClose() {
        this.screen.onClose();
    }

}
