package com.restdocs.toolwindow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.editor.colors.FontPreferences;
import com.intellij.openapi.editor.colors.impl.AppEditorFontOptions;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.OpenSourceUtil;
import com.zhaow.restful.ToolkitUtil;
import com.zhaow.restful.common.RequestHelper;
import com.zhaow.restful.highlight.JTextAreaHighlight;
import com.zhaow.restful.method.HttpMethod;
import com.zhaow.restful.navigation.action.RestServiceItem;
import com.zhaow.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.HorizontalLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.List;

//import com.restdocs.action.common.RestServiceItem;

public class RestServicesUI {

    private JPanel contentPanel = new JPanel();
    private JTree servicesTree;
    private JScrollPane serviceTreeScrollPane;
    private JLabel status;
    private JTextField query;
    private JPanel searchPanel;
    private JButton clear;
    private JTextArea serviceInfo;
//    private JTextArea requestParamsText;
    private JTextArea requestBodyTextArea;
    private JTextArea responseTextArea;
//    private JScrollPane inforScrollPane;
    private JSplitPane servicesContentPane;
    /**
     * 用 awt 重新定义，后期再改吧。
     */
    private JPanel serviceDetailPanel = new JPanel();
    private JTextField urlField;
    private JPanel urlPanel;
    private JTextField methodField;
    private JButton sendButton;
    private JTabbedPane requestTabbedPane;

//    private CopyRestUrlUtil copyRestUrlUtil;

    public RestServicesUI(Project project) {
        /*搜索框*/
        searchPanel.setLayout(new HorizontalLayout());
        query.setColumns(20);
        status.setBackground(Color.gray);

//        copyRestUrlUtil = new CopyRestUrlUtil(project);

        /* Service 信息详情（servicesContentPane） ， url，param，request*/
        /* Rest Service 树节点（serviceTree）*/

        servicesTree.setModel(null);
        servicesTree.setRootVisible(true);
        servicesTree.setCellRenderer(new RestTreeCellRenderer());
//        servicesTree.setCellRenderer(new MyServicesTreeCellRender());

        new TreeSpeedSearch(servicesTree, (o) -> {
            Object lastPathComponent = o.getLastPathComponent();
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
        },true

        );

//        new MyTreeSpeedSearch(servicesTree);

        serviceTreeScrollPane = new JBScrollPane(servicesTree, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        serviceTreeScrollPane.setBackground(Color.white);
        serviceTreeScrollPane.setBorder(BorderFactory.createEmptyBorder());

//        serviceDetailPanel#urlPanel
        urlPanel = new JPanel();
        urlPanel.setBorder(BorderFactory.createEmptyBorder());
        urlPanel.setLayout(new GridLayoutManager(1, 3));
        urlPanel.add(methodField,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));
        urlPanel.add(urlField,
                new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));

        urlPanel.add(sendButton,
                new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));


//        tabbedPanel
/*        serviceInfo.setRows(5);
        serviceInfo.setTabSize(10);
        serviceInfo.setLineWrap(true);
        serviceInfo.setWrapStyleWord(true);
        serviceInfo.setText("Service info");*/

        servicesContentPane.setDividerLocation(0.5);
        servicesContentPane.setLeftComponent(serviceTreeScrollPane);

//        serviceDetailPanel = new JPanel();
        serviceDetailPanel.setBorder(BorderFactory.createEmptyBorder());
        serviceDetailPanel.setLayout(new GridLayoutManager(2, 1));
        serviceDetailPanel.add(urlPanel,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));
//todo : 直接添加jtextarea组件
//        JScrollPane serviceRequestInfo = new JBScrollPane(serviceInfo, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        requestTabbedPane.add("RequestInfo", serviceRequestInfo);
//        requestTabbedPane.add("RequestInfo", serviceInfo);

        serviceDetailPanel.add(requestTabbedPane,
                new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));

        servicesContentPane.setRightComponent(serviceDetailPanel);

        contentPanel.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.setLayout(new GridLayoutManager(3, 1));
        contentPanel.add(searchPanel,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));
        contentPanel.add(servicesContentPane,
                new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null));
        contentPanel.add(status,
                new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null));

        addRequestParamsTab("Service = Info");
        bindMouseEvent(servicesTree);

        bindSendButtonActionListener();

        contentPanel.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

            }
        });
        contentPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                urlField.setSize(contentPanel.getWidth() - methodField.getWidth() - sendButton.getWidth() - 10,urlField.getHeight());
            }
        });


//sendButton.setBorder(new EmptyBorder(0,0,40,0));
        sendButton.setBorderPainted(false); // 隐藏边框

        servicesContentPane.setOneTouchExpandable(true);//让分割线显示出箭头
        servicesContentPane.setContinuousLayout(true);//操作箭头，重绘图形

    }



    private void bindSendButtonActionListener() {
        sendButton.addActionListener(e -> {

            String params = ToolkitUtil.textToRequestParam(serviceInfo.getText());
            String url = urlField.getText();

            if (params.length() != 0) {
                // 包含了参数
                if (url.contains("?")) {
                    url += "&" + params;
                } else {
                    url += "?" + params;
                }
            }

            // 完整url
            String method = methodField.getText();
            String responseText = url;

            Object response;
            if (requestBodyTextArea != null && StringUtils.isNotBlank(requestBodyTextArea.getText())) {
                response = RequestHelper.postRequestBodyWithJson(url, requestBodyTextArea.getText());
            }else if (method.equalsIgnoreCase("post")) {
                response = RequestHelper.post(url);
            } else {
//                com.zhaow.restful.common.RequestHelper.post(url);
                response = RequestHelper.get(url);
            }

            if (response != null) responseText = response.toString();

            addResponseTabPanel(responseText);
        });
    }


    private void bindMouseEvent(JTree servicesTree) {
        JPopupMenu popupMenu = createPopupMenu();

//        绑定事件
        servicesTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

// todo               begin
                int selRow = servicesTree.getRowForLocation(e.getX(), e.getY());
//                TreePath selPath = servicesTree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (e.getClickCount() == 1) {
//                        mySingleClick(selRow, selPath);
                    } else if (e.getClickCount() == 2) {
//                        myDoubleClick(selRow, selPath);
//NOTE: This example obtains both the path and row, but you only need to get the one you're interested in.
                        DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) getServicesTree().getLastSelectedPathComponent();

                        if (nodeSelected == null) return;

                        // rest 节点，跳转到相应位置
                        if (nodeSelected.getUserObject() instanceof RestServiceItem) {
                            RestServiceItem service = (RestServiceItem) nodeSelected.getUserObject();

                            OpenSourceUtil.navigate(service.getPsiMethod());
                        }
                    }
                }
// todo     end

                if (SwingUtilities.isRightMouseButton(e)) {

                    int row = servicesTree.getRowForLocation(e.getX(), e.getY());
                    if (row != -1) {
                        servicesTree.setSelectionRow(row);
                        DefaultMutableTreeNode selectedNode =
                                (DefaultMutableTreeNode) servicesTree.getLastSelectedPathComponent();

                        if (selectedNode.getUserObject() instanceof RestServiceItem) {
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());



                            final ActionManager actionManager = ActionManager.getInstance();
                            final ActionGroup actionGroup = (ActionGroup)actionManager.getAction("RestfulToolkitGroup");
                            if (actionGroup != null) {
                                JPopupMenu component = actionManager.createActionPopupMenu("", actionGroup).getComponent();

                                component.show(e.getComponent(), e.getX(), e.getY());
                            }
                        }

                    }
                }
            }
        });
    }

    public void addRequestParamsTab(String requestParams) {

        StringBuilder paramBuilder = new StringBuilder();
        String[] paramArray = requestParams.split("&");
        for (String paramPairStr : paramArray) {
            String[] paramPair = paramPairStr.split("=");

            String param = paramPair[0];
            String value = paramPairStr.substring(param.length() + 1);
            paramBuilder.append(param).append(" : ").append(value).append("\n");
        }

        if (serviceInfo == null){
            serviceInfo = createTextArea(paramBuilder.toString());
        }
        else {
            serviceInfo.setText(paramBuilder.toString());
        }

        highlightTextAreaData(serviceInfo);

        addRequestTabbedPane("RequestParams", serviceInfo);

    }

    public void addRequestBodyTabPanel(String text) {

//        jTextArea.setAutoscrolls(true);
        String reqBodyTitle = "RequestBody";
        if (requestBodyTextArea == null){
            requestBodyTextArea = createTextArea(text);
        }
        else {
            requestBodyTextArea.setText(text);
        }

        highlightTextAreaData(requestBodyTextArea);

        addRequestTabbedPane(reqBodyTitle, this.requestBodyTextArea);
    }

    /* 高亮 */
    public void highlightTextAreaData(JTextArea jTextArea) {
        JTextAreaHighlight.highlightTextAreaData(jTextArea);
    }

    @NotNull
    public JTextArea createTextArea(String text) {
        Font font = getEffectiveFont(text);

        JTextArea jTextArea = new JTextArea(text);
        jTextArea.setFont(font);

        jTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String text = jTextArea.getText();
                getEffectiveFont(text);
                highlightTextAreaData(jTextArea);
            }
        });

        jTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    // copy text to parse
                    CopyPasteManager.getInstance().setContents(new StringSelection(jTextArea.getText()));
                }
            }
        });

        highlightTextAreaData(jTextArea);

        return jTextArea;
    }

    @NotNull  // editor.xml
    private Font getEffectiveFont(String text) {
        FontPreferences fontPreferences = AppEditorFontOptions.getInstance().getFontPreferences();
        List<String> effectiveFontFamilies = fontPreferences.getEffectiveFontFamilies();

        int size = fontPreferences.getSize(fontPreferences.getFontFamily());
        Font font=new Font(FontPreferences.FALLBACK_FONT_FAMILY,Font.PLAIN,size);

        //有效字体
        for (String effectiveFontFamily : effectiveFontFamilies) {
            Font effectiveFont = new Font(effectiveFontFamily, Font.PLAIN, size);
            if (effectiveFont.canDisplayUpTo(text) == -1) {
                font = effectiveFont;
                break;
            }
        }
        return font;
    }

    public void addRequestTabbedPane(String title, JTextArea jTextArea) {

        JScrollPane jbScrollPane = new JBScrollPane(jTextArea, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jTextArea.addKeyListener(new TextAreaKeyAdapter(jTextArea));

        requestTabbedPane.addTab(title, jbScrollPane) ;

        requestTabbedPane.setSelectedComponent(jbScrollPane) ;//.setSelectedIndex(requestTabbedPane.getTabCount() - 1);
    }

    /*添加 Response Tab*/
    public void addResponseTabPanel(String text) {

        String responseTabTitle = "Response";
        if (responseTextArea == null) {
            responseTextArea = createTextArea(text);
            addRequestTabbedPane(responseTabTitle, responseTextArea);
        }
        else {
            Component componentAt = null;
            responseTextArea.setText(text);
            int tabCount = requestTabbedPane.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                if (requestTabbedPane.getTitleAt(i).equals(responseTabTitle)) {
                    componentAt = requestTabbedPane.getComponentAt(i);
                    requestTabbedPane.addTab(responseTabTitle,componentAt);
                    requestTabbedPane.setSelectedComponent(componentAt);
                    break;
//                    Component component = requestTabbedPane.getComponent(i);
                }
            }
            if (componentAt == null) {
                addRequestTabbedPane(responseTabTitle, responseTextArea);
            }

        }

    }

    @NotNull
    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyRestUrlAction = new JMenuItem("Copy REST Url");
        JMenuItem copyCurlAction = new JMenuItem("Copy cURL");
        popupMenu.add(copyRestUrlAction);
        popupMenu.add(copyCurlAction);

        copyRestUrlAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode =
                        (DefaultMutableTreeNode) servicesTree.getLastSelectedPathComponent();

                if (selectedNode.getUserObject() instanceof RestServiceItem) {
                    RestServiceItem serviceNode = (RestServiceItem) selectedNode.getUserObject();
                    String partialUrl = ((RestServiceItem) selectedNode.getUserObject()).getUrl();

//                    CopyPasteManager.getInstance().setContents(new StringSelection(copyRestUrlUtil.getFullUrl(partialUrl,
//                            serviceNode)));
                }
            }
        });

        copyCurlAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode =
                        (DefaultMutableTreeNode) servicesTree.getLastSelectedPathComponent();

                if (selectedNode.getUserObject() instanceof RestServiceItem) {
                    RestServiceItem serviceNode = (RestServiceItem) selectedNode.getUserObject();
                    String partialUrl = serviceNode.getUrl();

//                    String fullUrl = copyRestUrlUtil.getFullUrl(partialUrl, serviceNode);

                    StringBuilder curl = new StringBuilder("curl -X ");
                    curl.append(serviceNode.getMethod());
//                    curl.append(" " + fullUrl);

                    CopyPasteManager.getInstance().setContents(
                            new StringSelection(curl.toString()));
                }
            }
        });

        return popupMenu;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public JTree getServicesTree() {
        return servicesTree;
    }

    public void setStatusText(String status) {
        this.status.setText(status);
    }

    public JTextField getQuery() {
        return query;
    }

    public JButton getClear() {
        return clear;
    }


    public void setMethodValue(String method) {
        methodField.setText(String.valueOf(method));
    }

    public void setUrlValue(String url) {
        urlField.setText(url);
    }

    public void resetRequestTabbedPane() {
        this.requestTabbedPane.removeAll();
        resetTextComponent(serviceInfo);
        resetTextComponent(requestBodyTextArea);
        resetTextComponent(responseTextArea);
    }

    private void resetTextComponent(JTextArea textComponent) {
        if( textComponent != null && StringUtils.isNotBlank(textComponent.getText())) {
            textComponent.setText("");
        }
    }

    public JButton getSendButton() {
        return sendButton;
    }

    private class TextAreaKeyAdapter extends KeyAdapter {
        private final JTextArea jTextArea;

        public TextAreaKeyAdapter(JTextArea jTextArea) {
            this.jTextArea = jTextArea;
        }

        @Override
        public void keyPressed(KeyEvent event) {
            super.keyPressed(event);
            // 组合键ctrl+enter自定义，当Ctrl+Enter组合键按下时响应
            if ((event.getKeyCode() == KeyEvent.VK_ENTER)
                    && (event.isControlDown())) {
                //解析，格式化json
                String oldValue = jTextArea.getText();
                if (!JsonUtils.isValidJson(oldValue)) {
                    return;
                }

                JsonParser parser = new JsonParser();
                JsonElement parse = parser.parse(oldValue);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(parse);
                jTextArea.setText(json);
            }
        }
    }
}