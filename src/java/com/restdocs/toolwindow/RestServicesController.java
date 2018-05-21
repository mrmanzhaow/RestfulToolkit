package com.restdocs.toolwindow;


import com.intellij.openapi.project.Project;
import com.zhaow.restful.common.PsiMethodHelper;
import com.zhaow.restful.common.ServiceHelper;
import com.zhaow.restful.method.HttpMethod;
import com.zhaow.restful.navigation.action.RestServiceItem;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION;

public class RestServicesController {

    private RestServicesUI ui;
    private Map<String, List<RestServiceItem>> allServices;

    public void init(Project project) {
        ui = new RestServicesUI(project);
        allServices = loadServices(project);
        showServicesInTree(allServices);

        addListeners();
    }

    private Map<String, List<RestServiceItem>> loadServices(Project project) {
        return ServiceHelper.buildAllServicesGroupByModule(project);
    }

    private void showServicesInTree(Map<String, List<RestServiceItem>> services) {
        int totalServices = 0;

        JTree servicesTree = ui.getServicesTree();
//        servicesTree.getCellRenderer().
        servicesTree.setModel(null);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("REST Services");
        String rootName = "Found {serviceCount} services in {controllerCount} Controllers";

        //
        for (String moduleName : services.keySet()) {
            List<RestServiceItem> servicesByModule = services.get(moduleName);
            DefaultMutableTreeNode moduleNode = new DefaultMutableTreeNode(moduleName);

            Map<HttpMethod, List<RestServiceItem>> groupedByHttpMethod = servicesByModule.stream()
                    .collect(groupingBy(RestServiceItem::getMethod));

            for (HttpMethod method : groupedByHttpMethod.keySet()) {
                List<RestServiceItem> servicesByMethod = groupedByHttpMethod.get(method);
                DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(method);

                for (RestServiceItem service : servicesByMethod) {
                    DefaultMutableTreeNode restService = new DefaultMutableTreeNode(service);
                    methodNode.add(restService);
                    totalServices++;
                }

                moduleNode.add(methodNode);
            }
            root.add(moduleNode);
        }

        ui.setStatusText("  " + totalServices + (totalServices == 1 ? " service" : " services"));
        rootName = rootName.replace("{serviceCount}",  String.valueOf(totalServices));

//        String format = String.format(rootName, totalServices);
        root.setUserObject(rootName);
        addSelectionTreeListener(root);
    }

    private void addListeners() {
        JTextField query = ui.getQuery();

        ActionListener searchListener = e -> {
            String queryText = query.getText();
            filterRestServices(queryText);
        };

        query.addActionListener(searchListener);
        query.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println("insert---------------------------");
                filterRestServices(query.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                System.out.println("removeUpdate---------------------------");
                filterRestServices(query.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changedUpdate---------------------------");
                filterRestServices(query.getText());
            }
        });

        ui.getClear().addActionListener(e -> {
            query.setText("");
            showServicesInTree(allServices);
        });

        ui.getServicesTree().addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }

        });
        ui.getServicesTree().addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {

            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {

            }
        });
    }

    public void filterRestServices(String queryText) {
        Map<String, List<RestServiceItem>> filteredServices = new HashMap<>();

        for (String moduleName : allServices.keySet()) {

            List<RestServiceItem> services = allServices.get(moduleName)
                    .stream()
                    .filter(r -> r.matches(queryText))
                    .collect(Collectors.toList());

            if (services.size() > 0) {
                filteredServices.put(moduleName, services);
            }
        }

        showServicesInTree(filteredServices);
        expandAll(ui.getServicesTree());
    }


    private void addSelectionTreeListener(DefaultMutableTreeNode root) {
        DefaultTreeModel model = new DefaultTreeModel(root);
        ui.getServicesTree().setModel(model);

        ui.getServicesTree().getSelectionModel().setSelectionMode(SINGLE_TREE_SELECTION);

        // implicit override servicesTree TreeSelectionListener.valueChanged(TreeSelectionEvent e)
        ui.getServicesTree().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) ui.getServicesTree().getLastSelectedPathComponent();

            if (nodeSelected == null) return;

            // rest 节点，跳转到相应位置
            if (nodeSelected.getUserObject() instanceof RestServiceItem) {
                RestServiceItem service = (RestServiceItem) nodeSelected.getUserObject();
                showServiceDetail(service);
//                navigateToCode(service);
            }
        });
    }

    private void showServiceDetail(RestServiceItem service) {

        ui.resetRequestTabbedPane();

        ui.setMethodValue(String.valueOf(service.getMethod()));
        ui.setUrlValue(service.getFullUrl()) ;

        PsiMethodHelper psiMethodHelper = PsiMethodHelper.create(service.getPsiMethod());

        String requestParams = psiMethodHelper.buildParamString();
        if (StringUtils.isNotBlank(requestParams)) {
            ui.addRequestParamsTab(requestParams);
        }

        String requestBodyJson = psiMethodHelper.buildRequestBodyJson();
        if (StringUtils.isNotBlank(requestBodyJson)) {
            ui.addRequestBodyTabPanel(requestBodyJson);
        }

//        sendButton.setIcon(IconLoader.getIcon("/icons/get.png"));
//        sendButton.setContentAreaFilled(false); //透明的设置
//        sendButton.setBorderPainted(false); //去掉按钮的边框的设置

    }


    public RestServicesUI getUi() {
        return ui;
    }

    private void expandAll(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root));
    }

    private void expandAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path);
            }
        }
        tree.expandPath(parent);
    }

}
