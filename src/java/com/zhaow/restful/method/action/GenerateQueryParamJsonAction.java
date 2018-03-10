package com.zhaow.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.zhaow.restful.common.PsiMethodHelper;
import com.zhaow.restful.method.Parameter;

import java.awt.datatransfer.StringSelection;
import java.util.List;

/**
 * 生成Request Body JSON 字符串
 */
public class GenerateQueryParamJsonAction extends RestfulMethodSpringSupportedAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        //  @RequestBody entity 生成 json

        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) psiElement;
            PsiMethodHelper psiMethodHelper = PsiMethodHelper.create(psiMethod);
            List<Parameter> parameterList = psiMethodHelper.getParameterList();
//JavaShortClassNameIndex.getInstance().get("Product",myProject(e), GlobalSearchScope.projectScope(myProject(e)))
            for (Parameter parameter : parameterList) {
                if (parameter.isRequestBodyFound()) {
                    String queryJson = psiMethodHelper.buildRequestBodyJson(parameter);

                    CopyPasteManager.getInstance().setContents(new StringSelection(queryJson));

                    break;
                }
            }


        }
    }




}
