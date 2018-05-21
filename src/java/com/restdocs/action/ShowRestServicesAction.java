package com.restdocs.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.restdocs.toolwindow.RestServicesController;
import com.restdocs.toolwindow.RestServicesUI;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT;

public class ShowRestServicesAction extends AnAction {

    private static final String TOOL_WINDOW_ID = "REST Services";

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        loadServicesOnBackground(project);

        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
        if (toolWindow != null && toolWindow.isVisible()) {
            toolWindow.hide(null);
            ToolWindowManager.getInstance(project).unregisterToolWindow(TOOL_WINDOW_ID);
        }
    }

    private void loadServicesOnBackground(Project project) {
        ApplicationManager.getApplication().executeOnPooledThread(() ->
                ApplicationManager.getApplication().runReadAction(() ->
                        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Loading Rest Services") {
                            @Override
                            public void run(@NotNull ProgressIndicator indicator) {
                                indicator.setText("Searching for REST Services in your project...");

                                RestServicesController controller = new RestServicesController();
                                controller.init(project) ;

                                ApplicationManager.getApplication().invokeLater(() -> {
                                    registerToolWindow(project, controller.getUi());
                                });

                                System.out.println(controller.getUi().getContentPanel().getWidth());
                            }
                        })));
    }

    private void registerToolWindow(Project project, RestServicesUI ui) {
        String[] toolWindowIds = ToolWindowManager.getInstance(project).getToolWindowIds();

        boolean isToolWindowRegistered = Arrays.asList(toolWindowIds).stream().anyMatch(s -> s.equalsIgnoreCase(TOOL_WINDOW_ID));

        if (!isToolWindowRegistered) {
            ToolWindow toolWindow =
                    ToolWindowManager.getInstance(project).registerToolWindow(TOOL_WINDOW_ID, false, ToolWindowAnchor.RIGHT);

            toolWindow.getComponent().add(ui.getContentPanel());
            toolWindow.setTitle("REST Services");
            toolWindow.setIcon(AllIcons.CodeStyle.Gear);
            toolWindow.show(null);
        } else {
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
            toolWindow.show(null);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getData(PROJECT);
        e.getPresentation().setVisible(project != null);
    }
}