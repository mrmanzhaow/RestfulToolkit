package com.zhaow.restful.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.zhaow.restful.common.PsiClassHelper;

import java.awt.datatransfer.StringSelection;

public class ConvertClassToJSONCompressedAction extends AbstractBaseAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) psiElement;

            String json = PsiClassHelper.create(psiClass).convertClassToJSON(myProject(e),false);
            CopyPasteManager.getInstance().setContents(new StringSelection(json));
        }
    }

    @Override
    public void update(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        setActionPresentationVisible(e,psiElement instanceof PsiClass);
    }
}
