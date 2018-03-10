package com.zhaow.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

import java.awt.datatransfer.StringSelection;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT;

/**
 * 生成并复制restful url
 * tood: 没考虑RequestMapping 多个值的情况
 */
public class
GenerateFullUrlAction extends RestfulMethodSpringSupportedAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Module module = myModule(e);
        PsiElement psiElement = e.getData(PSI_ELEMENT);
        PsiMethod psiMethod = (PsiMethod) psiElement;
        ModuleHelper moduleHelper = ModuleHelper.create(module);

        String url = moduleHelper.buildFullUrlWithParams(psiMethod);

        CopyPasteManager.getInstance().setContents(new StringSelection(url));



    }

}
