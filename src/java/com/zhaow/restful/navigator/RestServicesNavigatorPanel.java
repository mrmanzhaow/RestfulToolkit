package com.zhaow.restful.navigator;


import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class RestServicesNavigatorPanel extends SimpleToolWindowPanel implements DataProvider {

    private final Project myProject;
    private final SimpleTree myTree;
    RestServiceDetail myRestServiceDetail;


    private JSplitPane servicesContentPaneJSplitPane;
    private Splitter servicesContentPaneSplitter ;
    private final JTextArea defaultUnderView = new JTextArea(" json format textarea ");


    public RestServicesNavigatorPanel(Project project, SimpleTree tree) {
        super(true, true);

        myProject = project;
        myTree = tree;
        myRestServiceDetail = project.getComponent(RestServiceDetail.class);

        final ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar("RestToolkit Navigator Toolbar",
                (DefaultActionGroup)actionManager
                        .getAction("Toolkit.NavigatorActionsToolbar"),
                true);
        setToolbar(actionToolbar.getComponent());

        Color gray = new Color(36,38,39);
//        setContent(ScrollPaneFactory.createScrollPane(myTree));

        myTree.setBorder(BorderFactory.createLineBorder(gray));
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(myTree);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.RED));


        servicesContentPaneSplitter = new Splitter(true,  .5f);
        servicesContentPaneSplitter.setShowDividerControls(true);
        servicesContentPaneSplitter.setDividerWidth(10);
        servicesContentPaneSplitter.setBorder(BorderFactory.createLineBorder(Color.RED));

        servicesContentPaneSplitter.setFirstComponent(scrollPane);

//        servicesContentPaneSplitter.setSecondComponent(defaultUnderView);

        servicesContentPaneSplitter.setSecondComponent(myRestServiceDetail);

        setContent(servicesContentPaneSplitter);

        /*servicesContentPaneJSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        servicesContentPaneJSplitPane.setDividerLocation(0.5);
        servicesContentPaneJSplitPane.setOneTouchExpandable(true);//让分割线显示出箭头
        servicesContentPaneJSplitPane.setLeftComponent(ScrollPaneFactory.createScrollPane(myTree));
        servicesContentPaneJSplitPane.setRightComponent(new JTextArea("fdasfdsafdsafdsafdssdf"));
        this.setContent(servicesContentPaneJSplitPane);*/
//        actionToolbar.setTargetComponent(servicesContentPaneSplitter);

        // popup
        myTree.addMouseListener(new PopupHandler() {
            public void invokePopup(final Component comp, final int x, final int y) {
                final String id = getMenuId(getSelectedNodes(RestServiceStructure.BaseSimpleNode.class));
                if (id != null) {
                    final ActionGroup actionGroup = (ActionGroup)actionManager.getAction(id);
                    if (actionGroup != null) {
                        JPopupMenu component = actionManager.createActionPopupMenu("", actionGroup).getComponent();

                        component.show(comp, x, y);
                    }
                }
            }

            @Nullable
            private String getMenuId(Collection<? extends RestServiceStructure.BaseSimpleNode> nodes) {
                String id = null;
                for (RestServiceStructure.BaseSimpleNode node : nodes) {
                    String menuId = node.getMenuId();
                    if (menuId == null) {
                        return null;
                    }
                    if (id == null) {
                        id = menuId;
                    }
                    else if (!id.equals(menuId)) {
                        return null;
                    }
                }
                return id;
            }
        });
    }

    private Collection<? extends RestServiceStructure.BaseSimpleNode> getSelectedNodes(Class<RestServiceStructure.BaseSimpleNode> aClass) {
        return RestServiceStructure.getSelectedNodes(myTree, aClass);
    }
}


