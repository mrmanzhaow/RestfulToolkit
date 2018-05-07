package com.zhaow.restful.common.resolver;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.zhaow.restful.annotations.JaxrsPathAnnotation;
import com.zhaow.restful.common.jaxrs.JaxrsAnnotationHelper;
import com.zhaow.restful.method.RequestPath;
import com.zhaow.restful.navigation.action.RestServiceItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;

public class JaxrsResolver extends BaseServiceResolver  {

    public JaxrsResolver(Module module) {
        myModule = module;
    }

    public JaxrsResolver(Project project) {
        myProject = project;
    }

    /*
    protected List<RestServiceItem> getServiceItemList(PsiMethod psiMethod) {
        List<RestServiceItem> itemList = new ArrayList<>();

        String classUriPath = JaxrsAnnotationHelper.getClassUriPath(psiMethod.getContainingClass());

        RequestPath[] methodUriPaths = JaxrsAnnotationHelper.getRequestPaths(psiMethod);

        for (RequestPath methodUriPath : methodUriPaths) {
            RestServiceItem item = createRestServiceItem(psiMethod, classUriPath, methodUriPath);
            itemList.add(item);
        }

        return itemList;
    }*/

    /*@NotNull
    public List<PsiMethod> getServicePsiMethodList(Project project, GlobalSearchScope globalSearchScope) {
        List<PsiMethod> psiMethodList = new ArrayList<>();

        for (PathMappingAnnotation supportedAnnotation : JaxrsPathAnnotation.values()) {

// 标注了 jaxrs Path 注解的类
            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(supportedAnnotation.getShortName(), project, globalSearchScope);

            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
                PsiElement psiElement = psiModifierList.getParent();
//                System.out.println("psiElement : "+ psiElement);

                if (!(psiElement instanceof PsiClass)) continue;

                PsiClass psiClass = (PsiClass) psiElement;
                PsiMethod[] psiMethods = psiClass.getMethods();

                if (psiMethods == null) {
                    continue;
                }

                psiMethodList.addAll(Arrays.asList(psiMethods));

            }
        }
        return psiMethodList;
    }*/


    @Override
    public List<RestServiceItem> getRestServiceItemList(Project project, GlobalSearchScope globalSearchScope) {
        List<RestServiceItem> itemList = new ArrayList<>();

//        for (PathMappingAnnotation supportedAnnotation : JaxrsPathAnnotation.values()) {

// 标注了 jaxrs Path 注解的类
//            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(supportedAnnotation.getShortName(), project, globalSearchScope);
            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(JaxrsPathAnnotation.PATH.getShortName(), project, globalSearchScope);

            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
                PsiElement psiElement = psiModifierList.getParent();

                if (!(psiElement instanceof PsiClass)) continue;

                PsiClass psiClass = (PsiClass) psiElement;
                PsiMethod[] psiMethods = psiClass.getMethods();

                if (psiMethods == null) {
                    continue;
                }

//                psiMethodList.addAll(Arrays.asList(psiMethods));

                String classUriPath = JaxrsAnnotationHelper.getClassUriPath(psiClass);

                for (PsiMethod psiMethod : psiMethods) {
                    RequestPath[] methodUriPaths = JaxrsAnnotationHelper.getRequestPaths(psiMethod);

                    for (RequestPath methodUriPath : methodUriPaths) {
                        RestServiceItem item = createRestServiceItem(psiMethod, classUriPath, methodUriPath);
                        itemList.add(item);
                    }
                }

            }
//        }


        return itemList;
    }
}
