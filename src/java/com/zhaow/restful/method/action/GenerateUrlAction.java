package com.zhaow.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.JBColor;
import com.zhaow.restful.common.PsiMethodHelper;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT;

/**
 * 生成并复制restful url
 * tood: 没考虑RequestMapping 多个值的情况
 */
public class GenerateUrlAction extends RestfulMethodBaseAction {
    Editor myEditor ;

    @Override
    public void actionPerformed(AnActionEvent e) {

        myEditor = e.getData(CommonDataKeys.EDITOR);
        PsiElement psiElement = e.getData(PSI_ELEMENT);
        PsiMethod psiMethod = (PsiMethod) psiElement;
        String servicePath = PsiMethodHelper.buildServicePath(psiMethod);
        CopyPasteManager.getInstance().setContents(new StringSelection(servicePath));
        showPopupBalloon("copied servicePath:\n " + servicePath);
    }

    private void showPopupBalloon(final String result) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                JBPopupFactory factory = JBPopupFactory.getInstance();
                factory.createHtmlTextBalloonBuilder(result, null, new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
                        .setFadeoutTime(5000)
                        .createBalloon()
                        .show(factory.guessBestPopupLocation(myEditor), Balloon.Position.above);
            }
        });
    }

}
