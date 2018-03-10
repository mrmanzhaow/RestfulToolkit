package com.zhaow.restful.common.spring;


import com.intellij.psi.*;
import com.zhaow.restful.annotations.SpringRequestMethodAnnotation;
import com.zhaow.restful.common.PsiAnnotationHelper;
import com.zhaow.restful.common.RestSupportedAnnotationHelper;
import com.zhaow.restful.method.RequestPath;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMappingAnnotationHelper implements RestSupportedAnnotationHelper {

    public static String[] getRequestMappingValues(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getModifierList().getAnnotations();
        if(annotations == null) return null;

        for (PsiAnnotation annotation : annotations) {
            if (annotation.getQualifiedName().equals(SpringRequestMethodAnnotation.REQUEST_MAPPING.getQualifiedName())) {
                return getRequestMappingValues(annotation);
            }
/*            //fixme: mac 下 annotation.getQualifiedName() 不是完整路径 ?
            if (annotation.getQualifiedName().equals(requestMapping.getShortName())) {
                return getRequestMappingValues(annotation);
            }*/
        }

        return new String[]{"/"};
    }

    /**
     * @param annotation
     * @param defaultValue
     * @return
     */
    private static List<RequestPath> getRequestMappings(PsiAnnotation annotation, String defaultValue) {
        List<RequestPath> mappingList = new ArrayList<>();

        SpringRequestMethodAnnotation requestAnnotation = SpringRequestMethodAnnotation.getByQualifiedName(annotation.getQualifiedName());

        if (requestAnnotation==null) {
            return new ArrayList<>();
        }

        List<String> methodList ;
        if (requestAnnotation.methodName() != null) {
            methodList = Arrays.asList(requestAnnotation.methodName()) ;
        } else { // RequestMapping 如果没有指定具体method，不写的话，默认支持所有HTTP请求方法
            methodList = PsiAnnotationHelper.getAnnotationAttributeValues(annotation, "method");
        }

        List<String> pathList = PsiAnnotationHelper.getAnnotationAttributeValues(annotation, "value");
        if (pathList.size() == 0) {
            pathList = PsiAnnotationHelper.getAnnotationAttributeValues(annotation, "path");
        }
        // 没有设置 value，默认方法名
        if (pathList.size() == 0) {
            pathList.add(defaultValue);
        }
        // todo: 处理没有设置 value 或 path 的 RequestMapping

//        List<String> finalPathList = pathList;
//        methodList.forEach(method-> finalPathList.forEach(path->mappingList.add(new RequestMapping(path,method))));

        if (methodList.size() > 0) {
            for (String method : methodList) {
                for (String path : pathList) {
                    mappingList.add(new RequestPath(path, method));
                }
            }
        } else {
            for (String path : pathList) {
                mappingList.add(new RequestPath(path, null));
            }
        }

        return mappingList;
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

        for (PsiAnnotation annotation : annotations) {
            for (SpringRequestMethodAnnotation mappingAnnotation : SpringRequestMethodAnnotation.values()) {
//            for (PathMappingAnnotation mappingAnnotation : PathMappingAnnotation.allPathMappingAnnotations) {
                if (annotation.getQualifiedName().equals(mappingAnnotation.getQualifiedName())) {
                    List<RequestPath> requestMappings = getRequestMappings(annotation, psiMethod.getName());
                    if (requestMappings.size()>0) {
                        list.addAll(requestMappings);
                    }
                }
            }
        }

        return list.toArray(new RequestPath[list.size()]);
    }


    public static String getRequestMappingValue(PsiAnnotation annotation) {
        String value = PsiAnnotationHelper.getAnnotationAttributeValue(annotation, "value");

//        String value = psiAnnotationMemberValue.getText().replace("\"","");
//        if(psiAnnotationMemberValue.)

        if (StringUtils.isEmpty(value))
            value = PsiAnnotationHelper.getAnnotationAttributeValue(annotation,"path");
        return value;
    }

    public static String[] getRequestMappingValues(PsiAnnotation annotation) {
        String[] values ;
        //一个value class com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl
        //多个value  class com.intellij.psi.impl.source.tree.java.PsiArrayInitializerMemberValueImpl
        PsiAnnotationMemberValue attributeValue = annotation.findDeclaredAttributeValue("value");

        List<String> value = PsiAnnotationHelper.getAnnotationAttributeValues(annotation, "value");


        if (attributeValue instanceof PsiLiteralExpression) {

            return  new String[]{((PsiLiteralExpression) attributeValue).getValue().toString()};
        }
        if (attributeValue instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) attributeValue).getInitializers();
            values = new String[initializers.length];

            for (PsiAnnotationMemberValue initializer : initializers) {

            }

            for (int i = 0; i < initializers.length; i++) {
                values[i] = ((PsiLiteralExpression)(initializers[i])).getValue().toString();
            }
        }

        return new String[]{};
    }


    public static String getOneRequestMappingPath(PsiClass psiClass) {
        // todo: 有必要 处理 PostMapping,GetMapping 么？
        PsiAnnotation annotation = psiClass.getModifierList().findAnnotation(SpringRequestMethodAnnotation.REQUEST_MAPPING.getQualifiedName());

        String path = null;
        if (annotation != null) {
            path = RequestMappingAnnotationHelper.getRequestMappingValue(annotation);
        }

        return path != null ? path : "";
    }


    public static String getOneRequestMappingPath(PsiMethod psiMethod) {
//        System.out.println("psiMethod:::::::" + psiMethod);
        SpringRequestMethodAnnotation requestAnnotation = null;

        List<SpringRequestMethodAnnotation> springRequestAnnotations = Arrays.stream(SpringRequestMethodAnnotation.values()).filter(annotation ->
                psiMethod.getModifierList().findAnnotation(annotation.getQualifiedName()) != null
        ).collect(Collectors.toList());

       /* if (springRequestAnnotations.size() == 0) {
            requestAnnotation = null;
        }*/

        if (springRequestAnnotations.size() > 0) {
            requestAnnotation = springRequestAnnotations.get(0);
        }

        String mappingPath;
        if(requestAnnotation != null){
            PsiAnnotation annotation = psiMethod.getModifierList().findAnnotation(requestAnnotation.getQualifiedName());
            mappingPath = RequestMappingAnnotationHelper.getRequestMappingValue(annotation);
        }else {
            String methodName = psiMethod.getName();
            mappingPath = StringUtils.uncapitalize(methodName);
        }

        return mappingPath;
    }



}