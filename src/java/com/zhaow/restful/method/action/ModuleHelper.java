package com.zhaow.restful.method.action;


import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiMethod;
import com.zhaow.restful.common.PsiMethodHelper;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

//PathAndQuery  AbsolutePath AbsoluteUri Query
public class ModuleHelper {
    Module module;

    // URL
    private static final String SCHEME = "http://"; //PROTOCOL
    private static final String HOST = "localhost";
    private static final String PORT = "8080"; // int
    private static String AUTHORITY = "http://localhost"+":"+PORT;
    private static final String PATH = "http://localhost"+":"+PORT; // PATH or FILE

    public static String getAUTHORITY() {
        return null;
    }

    PropertiesHandler propertiesHandler;

    public ModuleHelper(Module module) {
        this.module = module;
        propertiesHandler = new PropertiesHandler(module);
    }


    public static ModuleHelper create(Module module) {
        return new ModuleHelper(module);
    }

    /* 生成完整 URL , 附带参数 */
    @NotNull
    public String buildFullUrlWithParams(PsiMethod psiMethod) {

        String fullUrl = buildFullUrl(psiMethod);

        String params = PsiMethodHelper.create(psiMethod).buildParamString();

        // RequestMapping 注解设置了 param
        if (!params.isEmpty()) {
            StringBuilder urlBuilder = new StringBuilder(fullUrl);
            return urlBuilder.append(fullUrl.contains("?") ? "&": "?").append(params).toString();
        }
        return  fullUrl;
    }

    @NotNull
    public String buildFullUrl(PsiMethod psiMethod) {
        StringBuilder host = getServiceHostPrefix();

        String servicePath = PsiMethodHelper.buildServiceUriPath(psiMethod);

        return host.append(servicePath).toString();
    }

    @NotNull
    public StringBuilder getServiceHostPrefix() {
        String port = propertiesHandler.getServerPort();
        if (StringUtils.isEmpty(port)) port = PORT;

        String contextPath = propertiesHandler.getContextPath();
        return new StringBuilder(SCHEME).append(HOST).append(":").append(port).append(contextPath);
    }

}
