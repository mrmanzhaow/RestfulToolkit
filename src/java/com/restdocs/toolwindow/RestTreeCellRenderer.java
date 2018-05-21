package com.restdocs.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.UIUtil;
import com.zhaow.restful.method.HttpMethod;
import com.zhaow.restful.navigation.action.RestServiceItem;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.ArrayList;

//import com.restdocs.action.common.RestServiceItem;

public class RestTreeCellRenderer extends DefaultTreeCellRenderer {

    private JLabel label;

    public RestTreeCellRenderer() {
        label = new JLabel();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {

        UISettings uiSettings = UISettings.getInstance();
        Font font = new Font(uiSettings.getFontFace(), Font.PLAIN, uiSettings.getFontSize());

//        todo: begin
        SimpleTextAttributes attributes = null;
        String pattern = "";
//        String text = "getCampus";
        Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
        String text ;
        SimpleColoredComponent simpleColoredComponent = new SimpleColoredComponent();
        if (userObject instanceof RestServiceItem) {
            simpleColoredComponent.setIcon(AllIcons.FileTypes.Config);
            RestServiceItem restServiceNode = (RestServiceItem) userObject;
            text = restServiceNode.getUrl();
        } else if (userObject instanceof HttpMethod) {
            simpleColoredComponent.setIcon(AllIcons.CodeStyle.Gear);
            text = ((HttpMethod) userObject).name();
        } else {
            simpleColoredComponent.setIcon(AllIcons.Actions.Module);
            text = userObject.toString();
        }

        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + pattern, NameUtil.MatchingCaseSensitivity.NONE);

        Color treeBackground = UIUtil.getTreeForeground();//UIUtil.getEditorPaneBackground();
        Color selectedBg = UIUtil.getTreeSelectionForeground();
        final Iterable<TextRange> iterable = matcher.matchingFragments(text);
        if (iterable != null) {
            final Color fg = treeBackground;// attributes.getFgColor();
            final int style =SimpleTextAttributes.STYLE_PLAIN;// attributes.getStyle();
            final SimpleTextAttributes plain = new SimpleTextAttributes(style, fg);
            final SimpleTextAttributes highlighted = new SimpleTextAttributes(selectedBg, fg, null, style | SimpleTextAttributes.STYLE_SEARCH_MATCH);
//           simpleColoredComponent = new SimpleColoredComponent();
////            -----------------
//
            final java.util.List<Pair<String, Integer>> searchTerms = new ArrayList<>();
//
           for (TextRange fragment : iterable) {
                searchTerms.add(Pair.create(fragment.substring(text), fragment.getStartOffset()));
            }
//

            int lastOffset = 0;
//
            for (Pair<String, Integer> pair : searchTerms) {
                if (pair.second > lastOffset) {
                    simpleColoredComponent.append(text.substring(lastOffset, pair.second), plain);
                }
                simpleColoredComponent.append(text.substring(pair.second, pair.second + pair.first.length()), highlighted);
                lastOffset = pair.second + pair.first.length();
            }

            if (lastOffset < text.length()) {
                simpleColoredComponent.append(text.substring(lastOffset), plain);
            }
            simpleColoredComponent.setFont(font);
//            simpleColoredComponent.setIcon();
        } else{
            simpleColoredComponent = new SimpleColoredComponent();
            simpleColoredComponent.append(text);
        }

//   end



//ui.lnf.xml

        label.setFont(font);


//renderer.setTextSelectionColor(Color.BLACK);//设置当前选中节点的文本颜色
        if (userObject instanceof RestServiceItem) {
            RestServiceItem restServiceNode = (RestServiceItem) userObject;
            label.setIcon(AllIcons.FileTypes.Config);
            label.setText(restServiceNode.getUrl());
        } else if (userObject instanceof HttpMethod) {
            label.setIcon(AllIcons.CodeStyle.Gear);
            label.setText(((HttpMethod) userObject).name());
        } else {
            label.setIcon(AllIcons.Actions.Module);
            label.setText(userObject.toString());
        }


        return  simpleColoredComponent;
    }

//    tree.setRowHeight(20);//设置节点间的高度
}
