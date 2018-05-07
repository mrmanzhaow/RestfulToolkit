package com.zhaow.restful.navigation.action;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.zhaow.restful.common.ToolkitIcons;
import com.zhaow.restful.method.HttpMethod;
import com.zhaow.restful.method.action.ModuleHelper;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtNamedFunction;

import javax.swing.*;

//RequestMappingNavigationItem
public class RestServiceItem implements NavigationItem {
    private PsiMethod psiMethod; //元素
    private PsiElement psiElement; //元素
    private Module module;

    private String requestMethod; //请求方法 get/post...
    private HttpMethod method;  //请求方法 get/post...

    private String url; //url mapping;
/*
    private String methodName; //方法名称

    private String hostContextPath; // todo 处理 http://
    private PsiClass psiClass;
    private boolean foundRequestBody;*/

    private Navigatable navigationElement;
    //        ((KtClass) ((KtClassBody) psiElement.getParent()).getParent()).getModifierList().getAnnotationEntries().get(0).getText()
    public RestServiceItem(PsiElement psiElement, String requestMethod, String urlPath) {
        this.psiElement = psiElement;
        if (psiElement instanceof PsiMethod) {
            this.psiMethod = (PsiMethod) psiElement;
        }
        this.requestMethod = requestMethod;
        if (requestMethod != null) {
            method = HttpMethod.getByRequestMethod(requestMethod);
        }

        this.url = urlPath;
        if (psiElement instanceof Navigatable) {
            navigationElement = (Navigatable) psiElement;
        }
    }

    @Nullable
    @Override
    public String getName() {
//        return  /*this.requestMethod + " " +*/ this.urlPath;
        return  /*this.requestMethod + " " +*/ this.url;
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return new RestServiceItemPresentation();
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (navigationElement != null) {
            navigationElement.navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return navigationElement.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }


    /*匹配*/
    public boolean matches(String queryText) {
        String pattern = queryText;
        if (pattern.equals("/")) return true;

        com.intellij.psi.codeStyle.MinusculeMatcher matcher = com.intellij.psi.codeStyle.NameUtil.buildMatcher("*" + pattern, com.intellij.psi.codeStyle.NameUtil.MatchingCaseSensitivity.NONE);
        return matcher.matches(this.url);
    }

    private class RestServiceItemPresentation implements ItemPresentation {
        @Nullable
        @Override
        public String getPresentableText() {
//            return requestMethod  + " " + url;
            return url;
        }

//        对应的文件位置显示
        @Nullable
        @Override
        public String getLocationString() {
            String fileName = psiElement.getContainingFile().getName();

            String location = null;

            if (psiElement instanceof PsiMethod) {
                PsiMethod psiMethod = ((PsiMethod) psiElement);;
                location = psiMethod.getContainingClass().getName().concat("#").concat(psiMethod.getName());
            } else if (psiElement instanceof KtNamedFunction) {
                KtNamedFunction ktNamedFunction = (KtNamedFunction) RestServiceItem.this.psiElement;
                String className = ((KtClass) psiElement.getParent().getParent()).getName();
                location = className.concat("#").concat(ktNamedFunction.getName());
            }

            return "(" + location + ")";
        }

        @Nullable
        @Override
        public Icon getIcon(boolean unused) {
//            System.out.println(unused + "  " + this.getPresentableText());
            return ToolkitIcons.METHOD.get(method);
        }
    }

    public Module getModule() {
        return module;
    }

    public PsiMethod getPsiMethod() {
        return psiMethod;
    }

    public void setPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFullUrl() {
        if (module == null) {
            return getUrl();
        }

        ModuleHelper moduleHelper = ModuleHelper.create(module);
        // 处理 Mapping 设置个 value
//        String fullUrl = moduleHelper.buildFullUrl(psiMethod);

        return moduleHelper.getServiceHostPrefix() + getUrl();
    }

/*    public String getFullUrlWithParams() {
        ModuleHelper moduleHelper = ModuleHelper.create(module);
        String urlWithParams = moduleHelper.buildFullUrlWithParams(psiMethod);
        return urlWithParams;
    }*/

    public void  setModule(Module module) {
        this.module = module;
    }

/*    public String getHostContextPath() {
        return hostContextPath;
    }

    public boolean isFoundRequestBody() {
        return foundRequestBody;
    }

    public void setFoundRequestBody(boolean foundRequestBody) {
        this.foundRequestBody = foundRequestBody;
    }*/

    public PsiElement getPsiElement() {
        return psiElement;
    }
}
