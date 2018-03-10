package com.zhaow.restful.method.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import com.intellij.ui.JBColor;
import com.zhaow.restful.action.AbstractBaseAction;
import com.zhaow.restful.annotations.JaxrsHttpMethodAnnotation;
import com.zhaow.restful.annotations.JaxrsRequestAnnotation;
import com.zhaow.restful.annotations.SpringControllerAnnotation;
import com.zhaow.restful.annotations.SpringRequestMethodAnnotation;
import com.zhaow.restful.common.PsiMethodHelper;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT;

/**
 * 生成并复制restful url
 * todo: 没考虑RequestMapping 多个值的情况
 */
public class GenerateUrlAction /*extends RestfulMethodSpringSupportedAction*/ extends AbstractBaseAction {
    Editor myEditor ;

    @Override
    public void actionPerformed(AnActionEvent e) {

        myEditor = e.getData(CommonDataKeys.EDITOR);
        PsiElement psiElement = e.getData(PSI_ELEMENT);
        PsiMethod psiMethod = (PsiMethod) psiElement;

        //TODO: 需完善 jaxrs 支持
        String servicePath;
        if (isJaxrsRestMethod(psiMethod)) {
            servicePath = PsiMethodHelper.buildServiceUriPath(psiMethod);
        } else {
            servicePath = PsiMethodHelper.buildServiceUriPathWithParams(psiMethod);
        }

        CopyPasteManager.getInstance().setContents(new StringSelection(servicePath));
        showPopupBalloon("copied servicePath:\n " + servicePath);
    }

    private boolean isJaxrsRestMethod(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();

        for (PsiAnnotation annotation : annotations) {
            boolean match = Arrays.stream(JaxrsHttpMethodAnnotation.values()).map(sra -> sra.getQualifiedName()).anyMatch(name -> name.equals(annotation.getQualifiedName()));
            if(match) return match;
        }

        return false;
    }

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

        /*return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null ;*/

        return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(JaxrsRequestAnnotation.PATH.getQualifiedName()) != null ;
    }

    private boolean isRestfulMethod(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();

        for (PsiAnnotation annotation : annotations) {
            boolean match = Arrays.stream(SpringRequestMethodAnnotation.values()).map(sra -> sra.getQualifiedName()).anyMatch(name -> name.equals(annotation.getQualifiedName()));
            if(match) return match;
        }

        for (PsiAnnotation annotation : annotations) {
            boolean match = Arrays.stream(JaxrsHttpMethodAnnotation.values()).map(sra -> sra.getQualifiedName()).anyMatch(name -> name.equals(annotation.getQualifiedName()));
            if(match) return match;
        }

        return false;
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
