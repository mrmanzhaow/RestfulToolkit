package com.zhaow.restful.common.resolver;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.zhaow.restful.common.PsiMethodHelper;
import com.zhaow.restful.method.RequestPath;
import com.zhaow.restful.navigation.action.RestServiceItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseServiceResolver implements ServiceResolver{
    Module myModule;
    Project myProject;

    @Override
    public List<RestServiceItem> findAllSupportedServiceItemsInModule() {
        List<RestServiceItem> itemList = new ArrayList<>();
        if (myModule == null) {
            return itemList;
        }

        GlobalSearchScope globalSearchScope = GlobalSearchScope.moduleScope(myModule);

        List<PsiMethod> psiMethodList = this.getServicePsiMethodList(myModule.getProject(), globalSearchScope);

        //  尴尬了，PsiMethod 不能直接获取到 Module，暂时通过传参的方式
        psiMethodList.forEach(psiMethod -> {
            List<RestServiceItem> serviceItemList = getServiceItemList(psiMethod);
            itemList.addAll(serviceItemList);
        });
        return itemList;
    }


    @Override
    public List<RestServiceItem> findAllSupportedServiceItemsInProject() {
        List<RestServiceItem> itemList = new ArrayList<>();
        if(myProject == null && myModule != null){
            myProject = myModule.getProject();
        }

        if (myProject == null) {
            return itemList;
        }

        GlobalSearchScope globalSearchScope = GlobalSearchScope.projectScope(myProject);

        List<PsiMethod> psiMethodList = this.getServicePsiMethodList(myProject, globalSearchScope);

        for (PsiMethod psiMethod : psiMethodList) {
            List<RestServiceItem> singleMethodServices = getServiceItemList(psiMethod);

            itemList.addAll(singleMethodServices);

        }
        return itemList;

    }

    @NotNull
    protected RestServiceItem createRestServiceItem(PsiElement psiMethod, String classUriPath, RequestPath requestMapping) {
        if (!classUriPath.startsWith("/")) classUriPath = "/".concat(classUriPath);
        if (!classUriPath.endsWith("/")) classUriPath = classUriPath.concat("/");

        String methodPath = requestMapping.getPath();

        if (methodPath.startsWith("/")) methodPath = methodPath.substring(1, methodPath.length());
        String requestPath = classUriPath + methodPath;

        RestServiceItem item = new RestServiceItem(psiMethod, requestMapping.getMethod(), requestPath);
        if (myModule != null) {
            item.setModule(myModule);
        }
        return item;
    }


    protected abstract List<RestServiceItem> getServiceItemList(PsiMethod psiMethod);

    protected abstract List<PsiMethod> getServicePsiMethodList(Project myProject, GlobalSearchScope globalSearchScope);
}
