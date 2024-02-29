package net.p1nero.dialogue_lib.screen;

import net.p1nero.dialogue_lib.entity.Dialogueable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 *
 * 用多叉树来优化流式对话框（我自己起的名词，就是没有多个分支几乎都是一条直线的对话，不过好像带有分支的也可以用？
 * 好吧本来是打算带分支的但是发现有点难，打算以后再实现，结果现在多叉树变链表...
 * 从Command中得到启发{@link net.minecraft.commands.Commands}
 * @author LZY
 */
public class LinkListStreamDialogueScreenBuilder extends StreamDialogueScreenBuilder{

    private TreeNode answerNode;

    public LinkListStreamDialogueScreenBuilder(Entity entity) {
        super(entity);
    }

    /**
     * 重写这个是为了让你记得这才是Screen真正被调用的初始化的地方。建议在这里作些判断再调用start。
    * */
    @Override
    protected void init() {
    }

    /**
     * 初始化对话框，得先start才能做后面的操作
     * @param greeting 初始时显示的话
     */
    public LinkListStreamDialogueScreenBuilder start(Component greeting){
        answerRoot = new TreeNode(greeting);
        answerNode = answerRoot;
        return this;
    }

    /**
     * @param finalOption 最后显示的话
     * @param returnValue 选项的返回值，默认返回0。用于处理 {@link Dialogueable#handleNpcInteraction(Player, byte)}
     */
    public LinkListStreamDialogueScreenBuilder addFinalChoice(Component finalOption, byte returnValue){
        if(answerNode == null)
            return null;
        answerNode.addChild(new TreeNode.FinalNode(finalOption, returnValue));
        return this;
    }

    /**
     * 添加选项进树并返回下一个节点
     * @param option 该选项的内容
     * @param answer 选择该选项后的回答内容
     */
    public LinkListStreamDialogueScreenBuilder addChoice(Component option, Component answer){
        if(answerNode == null)
            return null;
        answerNode.addChild(answer,option);

        //直接下一个
        List<TreeNode> list = answerNode.getChildren();
        if(!(list.size() == 1 && list.get(0) instanceof TreeNode.FinalNode)){
            answerNode = list.get(0);
        }

        return this;
    }

}
