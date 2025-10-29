package com.p1nero.dialog_lib.client.screen.builder;

import com.p1nero.dialog_lib.api.component.DialogueComponentBuilder;
import com.p1nero.dialog_lib.api.component.DialogNode;
import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import com.p1nero.dialog_lib.client.screen.component.DialogueOptionComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 如果要链式对话就用start和addChoice
 * 如果要构建复杂对话就手动设置root
 */
public class DialogueScreenBuilder {

    protected DialogueScreen screen;
    protected Component defaultTitle = Component.empty();
    protected Component customTitle;
    @Nullable
    protected Entity entity;
    @Nullable
    protected BlockState blockState;
    @Nullable
    protected BlockPos pos;
    protected DialogueComponentBuilder builder;
    protected DialogNode root;
    protected DialogNode tempNode;
    protected String modId;
    protected Function<String, ? extends DialogueScreen> constructor = DialogueScreen::new;

    public DialogueScreenBuilder(Component defaultTitle, String name, String modId) {
        this.builder = new DialogueComponentBuilder(name, modId);
        this.defaultTitle = defaultTitle;
        this.modId = modId;
    }

    public DialogueScreenBuilder(BlockState blockState, BlockPos pos, String modId) {
        this.builder = new DialogueComponentBuilder(blockState, modId);
        this.blockState = blockState;
        this.pos = pos;
        this.modId = modId;
    }

    public DialogueScreenBuilder(Entity entity, String modId) {
        this.builder = new DialogueComponentBuilder(entity, modId);
        this.entity = entity;
        this.modId = modId;
    }

    /**
     * 可以设置用自己的窗口
     */
    public void setConstructor(Function<String, ? extends DialogueScreen> constructor) {
        this.constructor = constructor;
    }

    /**
     * 设置自定义显示名
     */
    public void setCustomTitle(Component customTitle) {
        this.customTitle = customTitle;
    }

    public String getModId() {
        return modId;
    }

    public boolean isEmpty() {
        return root == null;
    }

    /**
     * @deprecated 用{@link #buildWith(DialogNode)}替代
     */
    @Deprecated
    public void setRoot(DialogNode root) {
        this.root = root;
    }

    public void setScreen(DialogueScreen screen) {
        this.screen = screen;
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
    @Nullable
    public DialogueScreen build() {
        screen = constructor.apply(modId);
        if (root == null) {
            return null;
        }
        if (entity != null) {
            screen.setEntity(entity);
        }
        if(pos != null && blockState != null) {
            screen.setBlockState(blockState, pos);
        }
        if(customTitle != null) {
            screen.setCustomTitle(customTitle);
        }
        screen.setDialogueAnswer(root.getAnswer());
        List<DialogueOptionComponent> choiceList = new ArrayList<>();
        for (DialogNode child : root.getChildren()) {
            if (child.getOption() != null) {
                choiceList.add(new DialogueOptionComponent(child.getOption().copy(), createChoiceButton(child)));
            }
        }
        screen.setupDialogueOptions(choiceList);
        return screen;
    }

    /**
     * 递归添加按钮。放心如果遇到没有添加选项的节点会自动帮你添加一个返回空内容返回值为0的FinalNode。
     */
    private Button.OnPress createChoiceButton(DialogNode node) {

        //如果是终止按钮则实现返回效果
        if (node instanceof DialogNode.FinalNode finalNode) {
            return button -> {
                if (!screen.shouldRenderOption()) {
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
            if (!screen.shouldRenderOption()) {
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
            screen.setupDialogueOptions(choiceList);
        };
    }

}
