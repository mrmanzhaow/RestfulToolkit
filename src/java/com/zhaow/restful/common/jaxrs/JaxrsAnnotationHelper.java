package com.zhaow.restful.common.jaxrs;


import com.intellij.psi.*;

import com.zhaow.restful.annotations.JaxrsHttpMethodAnnotation;
import com.zhaow.restful.annotations.JaxrsPathAnnotation;
import com.zhaow.restful.common.PsiAnnotationHelper;
import com.zhaow.restful.method.RequestPath;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JaxrsAnnotationHelper {

    private static String getWsPathValue(PsiAnnotation annotation) {
        String value = PsiAnnotationHelper.getAnnotationAttributeValue(annotation, "value");

        return value != null ? value : "";
    }

    /**
     * 过滤所有注解
     * @param psiMethod
     * @return
     */
    public static RequestPath[] getRequestPaths(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();
        if(annotations == null) return null;
        List<RequestPath> list = new ArrayList<>();

        PsiAnnotation wsPathAnnotation = psiMethod.getModifierList().findAnnotation(JaxrsPathAnnotation.PATH.getQualifiedName());
        String path = wsPathAnnotation == null ? psiMethod.getName() : getWsPathValue(wsPathAnnotation);

        JaxrsHttpMethodAnnotation[] jaxrsHttpMethodAnnotations = JaxrsHttpMethodAnnotation.values();

        /*for (PsiAnnotation annotation : annotations) {
            for (JaxrsHttpMethodAnnotation methodAnnotation : jaxrsHttpMethodAnnotations) {
                if (annotation.getQualifiedName().equals(methodAnnotation.getQualifiedName())) {
                    list.add(new RequestPath(path, methodAnnotation.getShortName()));
                }
            }
        }*/

        Arrays.stream(annotations).forEach(a-> Arrays.stream(jaxrsHttpMethodAnnotations).forEach(methodAnnotation-> {
            if (a.getQualifiedName().equals(methodAnnotation.getQualifiedName())) {
                list.add(new RequestPath(path, methodAnnotation.getShortName()));
            }
        }));

        return list.toArray(new RequestPath[list.size()]);
    }


    public static String getClassUriPath(PsiClass psiClass) {
        PsiAnnotation annotation = psiClass.getModifierList().findAnnotation(JaxrsPathAnnotation.PATH.getQualifiedName());

        String path = PsiAnnotationHelper.getAnnotationAttributeValue(annotation, "value");

        return path != null ? path : "";
    }


    public static String getMethodUriPath(PsiMethod psiMethod) {
        JaxrsHttpMethodAnnotation requestAnnotation = null;

        List<JaxrsHttpMethodAnnotation> httpMethodAnnotations = Arrays.stream(JaxrsHttpMethodAnnotation.values()).filter(annotation ->
                psiMethod.getModifierList().findAnnotation(annotation.getQualifiedName()) != null
        ).collect(Collectors.toList());

       /* if (httpMethodAnnotations.size() == 0) {
            requestAnnotation = null;
        }*/

        if (httpMethodAnnotations.size() > 0) {
            requestAnnotation = httpMethodAnnotations.get(0);
        }

        String mappingPath;
        if(requestAnnotation != null){
            PsiAnnotation annotation = psiMethod.getModifierList().findAnnotation(JaxrsPathAnnotation.PATH.getQualifiedName());
            mappingPath = getWsPathValue(annotation);
        }else {
            String methodName = psiMethod.getName();
            mappingPath = StringUtils.uncapitalize(methodName);
        }

        return mappingPath;
    }

}