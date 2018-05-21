package com.restdocs.toolwindow;


import com.intellij.ui.SpeedSearchBase;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.util.ArrayUtilRt;
import com.intellij.util.ui.tree.TreeUtil;
import com.zhaow.restful.method.HttpMethod;
import com.zhaow.restful.navigation.action.RestServiceItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MyTreeSpeedSearch extends SpeedSearchBase<JTree> {
    private boolean myCanExpand = true;
    public MyTreeSpeedSearch(JTree component) {
        super(component);
    }

    @Override
    protected int getSelectedIndex() {
        if (myCanExpand) {
            return ArrayUtilRt.find(getAllElements(), myComponent.getSelectionPath());
        }
        int[] selectionRows = myComponent.getSelectionRows();
        return selectionRows == null || selectionRows.length == 0 ? -1 : selectionRows[0];
    }

    @Override
    protected Object[] getAllElements() {
        if (myCanExpand) {
            final Object root = myComponent.getModel().getRoot();
            if (root instanceof DefaultMutableTreeNode || root instanceof TreeSpeedSearch.PathAwareTreeNode) {
                final List<TreePath> paths = new ArrayList<>();
                TreeUtil.traverseDepth((TreeNode)root, node -> {
                    if (node instanceof DefaultMutableTreeNode) {
                        paths.add(new TreePath(((DefaultMutableTreeNode)node).getPath()));
                    }
                    else if (node instanceof TreeSpeedSearch.PathAwareTreeNode) {
                        paths.add(((TreeSpeedSearch.PathAwareTreeNode)node).getPath());
                    }
                    return true;
                });
                return paths.toArray(new TreePath[paths.size()]);
            }
        }
        TreePath[] paths = new TreePath[myComponent.getRowCount()];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = myComponent.getPathForRow(i);
        }
        return paths;
    }

    @Nullable
    @Override
    protected String getElementText(Object element) {
        TreePath path = (TreePath)element;
        String string = path.toString();
        Object lastPathComponent = path.getLastPathComponent();

        if (!(lastPathComponent instanceof DefaultMutableTreeNode)) {
            return null;
        } else {
            Object userObject = ((DefaultMutableTreeNode)lastPathComponent).getUserObject();
//                return (String)projectsNameMap.get(userObject);
            String text ;
            if (userObject instanceof RestServiceItem) {
                RestServiceItem restServiceNode = (RestServiceItem) userObject;
                text = restServiceNode.getUrl();
            } else if (userObject instanceof HttpMethod) {
                text = ((HttpMethod) userObject).name();
            } else {
                text = userObject.toString();
            }

            return text;
        }
    }

    @Override
    protected void selectElement(Object element, String selectedText) {
        TreeUtil.selectPath(myComponent, (TreePath)element);
    }

    @NotNull
    private List<TreePath> findAllFilteredElements(String s) {
        List<TreePath> paths = new ArrayList<>();
        String _s = s.trim();

        ListIterator<Object> it = getElementIterator(0);
        while (it.hasNext()) {
            Object element = it.next();
            if (isMatchingElement(element, _s)) paths.add((TreePath)element);
        }
        return paths;
    }



}
