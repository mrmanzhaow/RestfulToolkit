package com.zhaow.restful.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;


public abstract class AbstractBaseAction extends AnAction {

    protected Module myModule(AnActionEvent e) {
        return e.getData(DataKeys.MODULE);
    }

    protected Project myProject(AnActionEvent e) {
        return getEventProject(e);
    }

    /**
     * 设置触发有效条件
     * @param e
     * @param visible
     */
    protected void setActionPresentationVisible(AnActionEvent e, boolean visible) {
        e.getPresentation().setVisible(visible);
    }

}
