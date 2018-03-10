package com.zhaow.restful.common;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.zhaow.restful.annotations.SpringControllerAnnotation;
import com.zhaow.restful.navigation.action.RestServiceItem;
import com.zhaow.restful.navigator.RestServiceProject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 服务相关工具类
 */
public class ServiceHelper {
    public static final Logger LOG = Logger.getInstance(ServiceHelper.class);
    PsiMethod psiMethod;

    public ServiceHelper(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    @NotNull
    public static List<PsiMethod> getServicePsiMethodList(Project project, GlobalSearchScope globalSearchScope) {
        List<PsiMethod> psiMethodList = new ArrayList<>();
        for (SpringControllerAnnotation controllerAnnotations : SpringControllerAnnotation.values()) {

// 标注了 (Rest)Controller 注解的类，即 Controller 类
            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(controllerAnnotations.getShortName(), project, globalSearchScope);

            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
                PsiElement psiElement = psiModifierList.getParent();
                //
                /*if (!(psiElement instanceof PsiClass)) continue;*/

//                String[] controllerPaths = RequestMappingAnnotationHelper.getRequestMappingValues(psiModifierList.getAnnotations());

                //查找controller中  所有包含了RequestMapping的方法
                PsiClass psiClass = (PsiClass) psiElement;
                PsiMethod[] psiMethods = psiClass.getMethods();
// FIXME: 这里应该只包含 设置了，requstmapping的方法，除非所有方法都没标注 requstmapping

                if (psiMethods == null) {
                    continue;
                }

                for (PsiMethod psiMethod : psiMethods) {
                    // todo: 没有处理同时标注了 GET 和 POST 两种方法的方法，定义一个 RequestMapping 类{method，path}
                    psiMethodList.add(psiMethod);
                }
            }
        }
        return psiMethodList;
    }

/*    public static Map<String, List<RestServiceItem>> buildAllServicesGroupByModule(Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        Map<String, List<RestServiceItem>> listMap = new HashMap<>();

        for (Module module : modules) {
            List<RestServiceItem> restServices = buildRestServiceItemList(module);
            if (restServices.size() > 0) {
                listMap.put(module.getName(), restServices);
            }
        }

        return listMap;
    }*/

    public static List<RestServiceProject> buildRestServiceProjectList(Project project) {
        System.out.println("buildRestServiceProjectList");
        List<RestServiceProject> serviceProjectList = new ArrayList<>();

        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            List<RestServiceItem> restServices = buildRestServiceItemList(module);
            if (restServices.size() > 0) {
                serviceProjectList.add(new RestServiceProject(module, restServices));
            }
        }

        return serviceProjectList;
    }


    @NotNull
    public static List<RestServiceItem> buildRestServiceItemList(Project project) {
        List<RestServiceItem> itemList = new ArrayList<>();

        GlobalSearchScope globalSearchScope = GlobalSearchScope.projectScope(project);

        List<PsiMethod> psiMethodList = ServiceHelper.getServicePsiMethodList(project, globalSearchScope);

        for (PsiMethod psiMethod : psiMethodList) {

            List<RestServiceItem> singleMethodServices = PsiMethodHelper.create(psiMethod).getServiceItemList();

            itemList.addAll(singleMethodServices);

        }
        return itemList;
    }

    public static List<RestServiceItem> buildRestServiceItemList(Module module) {
        GlobalSearchScope globalSearchScope = GlobalSearchScope.moduleScope(module);

        List<PsiMethod> psiMethodList = ServiceHelper.getServicePsiMethodList(module.getProject(), globalSearchScope);

        List<RestServiceItem> itemList = new ArrayList<>();
        //  尴尬了，PsiMethod 不能直接获取到 Module，暂时通过传参的方式
        psiMethodList.forEach(psiMethod -> {
            List<RestServiceItem> serviceItemList = PsiMethodHelper.create(psiMethod).withModule(module).getServiceItemList();
            itemList.addAll(serviceItemList);
        });

//        for (PsiMethod psiMethod : psiMethodList) {
//            List<RestServiceItem> serviceItemList = PsiMethodHelper.create(psiMethod).getServiceItemList();
//
//            if (serviceItemList != null && serviceItemList.size()>0) {
//                itemList.addAll(serviceItemList);
//            }
//        }

        return itemList;
    }


    private static List<RestServiceItem> buildRestServiceItemList(List<PsiMethod> psiMethodList) {

        List<RestServiceItem> itemList = new ArrayList<>();

        //  尴尬了，PsiMethod 不能直接获取到 Module，暂时通过传参的方式
        psiMethodList.forEach(psiMethod -> {
            List<RestServiceItem> serviceItemList = PsiMethodHelper.create(psiMethod).getServiceItemList();
            itemList.addAll(serviceItemList);
        });

//        for (PsiMethod psiMethod : psiMethodList) {
//            List<RestServiceItem> serviceItemList = PsiMethodHelper.create(psiMethod).getServiceItemList();
//
//            if (serviceItemList != null && serviceItemList.size()>0) {
//                itemList.addAll(serviceItemList);
//            }
//        }
        return itemList;
    }
}
