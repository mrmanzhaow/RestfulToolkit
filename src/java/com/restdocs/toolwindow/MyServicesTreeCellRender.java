package com.restdocs.toolwindow;


import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.zhaow.restful.method.HttpMethod;
import com.zhaow.restful.navigation.action.RestServiceItem;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class MyServicesTreeCellRender extends NodeRenderer {
    String pattern;

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
/*        if (userObject instanceof ModulesDependenciesPanel.MyUserObject) {
            ModulesDependenciesPanel.MyUserObject node = (ModulesDependenciesPanel.MyUserObject)userObject;
            this.setIcon(ModuleType.get(node.myModule).getIcon());
            this.append(node.myModule.getName(), node.myInCycle ? SimpleTextAttributes.ERROR_ATTRIBUTES : SimpleTextAttributes.REGULAR_ATTRIBUTES);
        } else if (userObject != null) {
            this.append(userObject.toString(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        }*/



        String text ;
        if (userObject instanceof RestServiceItem) {
            this.setIcon(AllIcons.FileTypes.Config);
            RestServiceItem restServiceNode = (RestServiceItem) userObject;
            text = restServiceNode.getUrl();
            this.append(text);
        } else if (userObject instanceof HttpMethod) {
            this.setIcon(AllIcons.CodeStyle.Gear);
            text = ((HttpMethod) userObject).name();
            this.append(text);
        } else {
            this.setIcon(AllIcons.Actions.Module);
            text = userObject.toString();
//            this.append(text);
//            this.append(userObject.toString(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
            this.append(text, SimpleTextAttributes.REGULAR_ATTRIBUTES);
        }

        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);

    }
}
