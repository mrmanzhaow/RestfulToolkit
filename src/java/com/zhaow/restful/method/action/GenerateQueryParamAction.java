package com.zhaow.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiUtil;
import com.zhaow.restful.common.PsiMethodHelper;

import java.awt.datatransfer.StringSelection;

/**
 * 生成查询参数
 */
public class GenerateQueryParamAction extends RestfulMethodBaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        PsiUtil.getTopLevelClass(psiElement);
        if (psiElement instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) psiElement;
            String params = PsiMethodHelper.create(psiMethod).buildParamString();

            CopyPasteManager.getInstance().setContents(new StringSelection(params));
        }

    }

}
