package com.zhaow.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.zhaow.restful.common.PsiMethodHelper;
import org.jetbrains.kotlin.asJava.LightClassUtilsKt;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtNamedFunction;

import java.awt.datatransfer.StringSelection;
import java.util.List;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT;

/**
 * 生成并复制restful url
 * tood: 没考虑RequestMapping 多个值的情况
 */
public class
GenerateFullUrlAction extends SpringAnnotatedMethodAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Module module = myModule(e);
        PsiElement psiElement = e.getData(PSI_ELEMENT);
        if (psiElement instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) psiElement;

            ModuleHelper moduleHelper = ModuleHelper.create(module);

//            String url = moduleHelper.buildFullUrlWithParams(psiMethod);

            String url = PsiMethodHelper.create(psiMethod).withModule(module).buildFullUrlWithParams();

            CopyPasteManager.getInstance().setContents(new StringSelection(url));
        }

        if (psiElement instanceof KtNamedFunction) {
            KtNamedFunction ktNamedFunction = (KtNamedFunction) psiElement;
            PsiElement parentPsi = psiElement.getParent().getParent();
            if (parentPsi instanceof KtClassOrObject) {
//                KtLightClass ktLightClass = LightClassUtilsKt.toLightClass(((KtClassOrObject) parentPsi));

                List<PsiMethod> psiMethods = LightClassUtilsKt.toLightMethods(ktNamedFunction);
                PsiMethod psiMethod = psiMethods.get(0);
                ModuleHelper moduleHelper = ModuleHelper.create(module);

                String url = PsiMethodHelper.create(psiMethod).withModule(module).buildFullUrlWithParams();

                CopyPasteManager.getInstance().setContents(new StringSelection(url));

            }

        }

    }

}
