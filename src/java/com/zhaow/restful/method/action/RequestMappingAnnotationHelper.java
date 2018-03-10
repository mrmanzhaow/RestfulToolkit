package com.zhaow.restful.method.action;


import com.intellij.psi.*;
import com.zhaow.restful.annotations.SpringRequestAnnotation;
import com.zhaow.restful.method.RequestMapping;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestMappingAnnotationHelper {

    public static String getRequestMappingValue(PsiAnnotation annotation) {
        String value = null;
        //只有注解
        //一个值 class com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl
        //两个值  class com.intellij.psi.impl.source.tree.java.PsiArrayInitializerMemberValueImpl
        PsiAnnotationMemberValue attributeValue = annotation.findDeclaredAttributeValue("value");

        if (attributeValue instanceof PsiLiteralExpression) {
            value = ((PsiLiteralExpression) attributeValue).getValue().toString();
        }
        if (attributeValue instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) attributeValue).getInitializers();
            value = ((PsiLiteralExpression) (initializers[0])).getValue().toString();
        }

//        String value = psiAnnotationMemberValue.getText().replace("\"","");
//        if(psiAnnotationMemberValue.)

        if (StringUtils.isEmpty(value)) value = annotation.findAttributeValue("path").getText();
        return value;
    }


    public static String[] getRequestMappingValues(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getModifierList().getAnnotations();
        if(annotations == null) return null;

        for (PsiAnnotation annotation : annotations) {
            SpringRequestAnnotation requestMapping = SpringRequestAnnotation.REQUEST_MAPPING;
            if (annotation.getQualifiedName().equals(requestMapping.getQualifiedName())) {
                return getRequestMappingValues(annotation);
            }
            //fixme: mac 下 annotation.getQualifiedName() 不是完整路径 ?
            if (annotation.getQualifiedName().equals(requestMapping.getShortName())) {
                return getRequestMappingValues(annotation);
            }
        }

        return new String[]{"/"};
    }

    /**
     * @param annotation
     * @param defaultValue
     * @return
     */
    private static List<RequestMapping> getRequestMappings(PsiAnnotation annotation, String defaultValue) {
        List<RequestMapping> mappingList = new ArrayList<>();

        SpringRequestAnnotation requestAnnotation = SpringRequestAnnotation.getByQualifiedName(annotation.getQualifiedName());

        if (requestAnnotation==null) {
            return new ArrayList<>();
        }

        List<String> methodList ;
        if (requestAnnotation.methodName() != null) {
            methodList = Arrays.asList(requestAnnotation.methodName()) ;
        } else { // RequestMapping 如果没有指定具体method，不写的话，默认支持所有HTTP请求方法
            methodList = getAnnotationAttributeValues(annotation, "method");
        }


        List<String> pathList = getAnnotationAttributeValues(annotation, "value");
        if (pathList.size() == 0) {
            pathList = getAnnotationAttributeValues(annotation, "path");
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
                    mappingList.add(new RequestMapping(path, method));
                }
            }
        } else {
            for (String path : pathList) {
                mappingList.add(new RequestMapping(path, null));
            }
        }

        return mappingList;
    }

    /**
     * 过滤所有注解
     * @param psiMethod
     * @return
     */
    public static RequestMapping[] getRequestMappings(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getModifierList().getAnnotations();
        if(annotations == null) return null;
        List<RequestMapping> list = new ArrayList<>();

        for (PsiAnnotation annotation : annotations) {
/*            Arrays.stream(SpringRequestAnnotation.values()).filter(sra -> annotation.getQualifiedName().equals(sra.getQualifiedName()))
                    .forEach(a -> {List<RequestMapping> requestMappings = getRequestMappings(annotation);
                        if (requestMappings.size()>0) {
                            list.addAll(requestMappings);
                        }
                    });*/

            for (SpringRequestAnnotation springRequestAnnotation : SpringRequestAnnotation.values()) {
                if (annotation.getQualifiedName().equals(springRequestAnnotation.getQualifiedName())) {
                    List<RequestMapping> requestMappings = getRequestMappings(annotation, psiMethod.getName());
                    if (requestMappings.size()>0) {
                        list.addAll(requestMappings);
                    }
                }
            }
        }

        return list.toArray(new RequestMapping[list.size()]);
    }


    public static String[] getRequestMappingValues(PsiAnnotation annotation) {
        String[] values ;
        //一个value class com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl
        //多个value  class com.intellij.psi.impl.source.tree.java.PsiArrayInitializerMemberValueImpl
        PsiAnnotationMemberValue attributeValue = annotation.findDeclaredAttributeValue("value");

        if (attributeValue instanceof PsiLiteralExpression) {

            return  new String[]{((PsiLiteralExpression) attributeValue).getValue().toString()};
        }
        if (attributeValue instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) attributeValue).getInitializers();
            values = new String[initializers.length];

            for (int i = 0; i < initializers.length; i++) {
                values[i] = ((PsiLiteralExpression)(initializers[i])).getValue().toString();
            }
        }

        return new String[]{};
    }


    @NotNull
    private static List<String> getAnnotationAttributeValues(PsiAnnotation annotation, String attr) {
        PsiAnnotationMemberValue psiPathMember = annotation.findDeclaredAttributeValue(attr);
        return getAnnotationAttributeValues(psiPathMember);
    }

    @NotNull
    private static List<String> getAnnotationAttributeValues(PsiAnnotationMemberValue value) {
        List<String> values = new ArrayList<>();
        if (value instanceof PsiReferenceExpression) {
            PsiReferenceExpression expression = (PsiReferenceExpression) value;
            values.add(expression.getText());
        } else if (value instanceof PsiLiteralExpression) {
//            values.add(psiNameValuePair.getLiteralValue());
            values.add(((PsiLiteralExpression) value).getValue().toString());
        } else if (value instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) value).getInitializers();

            for (PsiAnnotationMemberValue initializer : initializers) {
                values.add(initializer.getText().replaceAll("\\\"", ""));
            }
        }
        return values;
    }

}