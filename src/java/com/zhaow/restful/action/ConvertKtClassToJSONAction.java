package com.zhaow.restful.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.psi.KtClass;

//import com.intellij.psi.PsiClass;
//import com.zhaow.restful.common.PsiClassHelper;

public class ConvertKtClassToJSONAction extends AbstractBaseAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement instanceof KtClass) {

            /*String json = KtClassHelper.create((KtClass) psiElement).convertClassToJSON(myProject(e), true);
            CopyPasteManager.getInstance().setContents(new StringSelection(json));*/
        }
    }

    @Override
    public void update(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        setActionPresentationVisible(e,psiElement instanceof KtClass);
    }
}
