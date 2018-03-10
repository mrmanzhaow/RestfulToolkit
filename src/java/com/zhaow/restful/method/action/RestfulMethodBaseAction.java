package com.zhaow.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.*;
import com.zhaow.restful.action.AbstractBaseAction;
import com.zhaow.restful.annotations.SpringControllerAnnotation;
import com.zhaow.restful.annotations.SpringRequestAnnotation;

import java.util.Arrays;

/**
 * Restful method （restful 方法添加方法 ）
 */
public abstract class RestfulMethodBaseAction extends AbstractBaseAction {

    /**
     * spring rest 方法被选中才触发
     * @param e
     */
    @Override
    public void update(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);

        boolean visible = false;

        if(psiElement instanceof PsiMethod){
            PsiMethod psiMethod = (PsiMethod) psiElement;
            // rest method 或标注了RestController 注解
            visible =  (isRestController(psiMethod.getContainingClass()) || isRestfulMethod(psiMethod) );
        }

        setActionPresentationVisible(e, visible);
    }

    //包含 "RestController" "Controller"
    private boolean isRestController(PsiClass containingClass) {
        PsiModifierList modifierList = containingClass.getModifierList();

        return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null ;
    }

    private boolean isRestfulMethod(PsiMethod psiMethod) {
        return  containsSpringRequestAnnotation(psiMethod);
    }


    private boolean containsSpringRequestAnnotation(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();

        for (PsiAnnotation annotation : annotations) {
            boolean match = Arrays.stream(SpringRequestAnnotation.values()).map(sra -> sra.getQualifiedName()).anyMatch(name -> name.equals(annotation.getQualifiedName()));
            if(match) return match;
        }

        return false;
    }


}
