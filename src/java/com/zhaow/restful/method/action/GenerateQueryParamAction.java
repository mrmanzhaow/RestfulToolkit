package com.zhaow.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiUtil;
import com.zhaow.restful.common.PsiMethodHelper;
import org.jetbrains.kotlin.asJava.LightClassUtilsKt;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtNamedFunction;

import java.awt.datatransfer.StringSelection;
import java.util.List;

/**
 * 生成查询参数
 */
public class GenerateQueryParamAction extends SpringAnnotatedMethodAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        PsiUtil.getTopLevelClass(psiElement);
        PsiMethod psiMethod = null;
        if (psiElement instanceof PsiMethod) {
            psiMethod = (PsiMethod) psiElement;
        }

        if (psiElement instanceof KtNamedFunction) {
            KtNamedFunction ktNamedFunction = (KtNamedFunction) psiElement;
            PsiElement parentPsi = psiElement.getParent().getParent();
            if (parentPsi instanceof KtClassOrObject) {
//                KtLightClass ktLightClass = LightClassUtilsKt.toLightClass(((KtClassOrObject) parentPsi));

                List<PsiMethod> psiMethods = LightClassUtilsKt.toLightMethods(ktNamedFunction);
//                String params = PsiMethodHelper.create(psiMethods.get(0)).buildParamString();
                psiMethod = psiMethods.get(0);
//                String buildParamString = KtFunctionHelper.create(ktNamedFunction).buildParamString();
            }
        }

        if (psiMethod != null) {
            String params = PsiMethodHelper.create(psiMethod).buildParamString();

            CopyPasteManager.getInstance().setContents(new StringSelection(params));
        }

    }

}
