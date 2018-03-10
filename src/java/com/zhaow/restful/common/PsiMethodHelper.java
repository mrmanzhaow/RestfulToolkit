package com.zhaow.restful.common;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.zhaow.restful.annotations.JaxrsRequestAnnotation;
import com.zhaow.restful.annotations.SpringControllerAnnotation;
import com.zhaow.restful.common.jaxrs.JaxrsAnnotationHelper;
import com.zhaow.restful.method.Parameter;
import com.zhaow.restful.common.spring.RequestMappingAnnotationHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.zhaow.restful.annotations.SpringRequestParamAnnotations.*;

/**
 * PsiMethod处理类
 */
public class PsiMethodHelper {
    PsiMethod psiMethod;
    Project myProject;
    Module myModule;

    private String pathSeparator= "/";

    public static PsiMethodHelper create(@NotNull PsiMethod psiMethod) {
        return new PsiMethodHelper(psiMethod);
    }

    public PsiMethodHelper withModule(Module module) {
        this.myModule = module;
        return this;
    }

    protected PsiMethodHelper(@NotNull PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    @NotNull
    protected Project getProject() {
        myProject =  psiMethod.getProject();
        return myProject;
    }

    /**
     * 构建URL参数 key value
     * @return
     */
    public String buildParamString() {

//        boolean matchedGet = matchGetMethod();
        // 没指定method 标示支持所有method

        StringBuilder param = new StringBuilder("");
        Map<String, Object> baseTypeParamMap = getBaseTypeParameterMap();

        if (baseTypeParamMap != null && baseTypeParamMap.size() > 0) {
            baseTypeParamMap.forEach((s, o) -> param.append(s).append("=").append(o).append("&"));
        }

        return param.length() >0 ? param.deleteCharAt(param.length()-1).toString() : "";
    }

    /*获取方法中基础类型（primitive和string、date等以及这些类型数组）*/
    @NotNull
    public Map<String, Object> getBaseTypeParameterMap() {
        List<Parameter> parameterList = getParameterList();

        Map<String,Object> baseTypeParamMap = new LinkedHashMap();

        // 拼接参数
        for (Parameter parameter : parameterList) {
//跳过标注 RequestBody 注解的参数
            if (parameter.isRequestBodyFound()) {
                continue;
            }

            // todo 判断类型
            // 8 PsiPrimitiveType
            // 8 boxed types; String,Date:PsiClassReferenceType == field.getType().getPresentableText()
            String shortTypeName = parameter.getShortTypeName();
            Object defaultValue = PsiClassHelper.getJavaBaseTypeDefaultValue(shortTypeName);
            //简单常用类型
            if (defaultValue != null) {
                baseTypeParamMap.put(parameter.getParamName(),(defaultValue));
                continue;
            }

            PsiClassHelper psiClassHelper = PsiClassHelper.create(psiMethod.getContainingClass());
            PsiClass psiClass = psiClassHelper.findOnePsiClassByClassName(parameter.getParamType(), getProject());
            if (psiClass != null) {
                PsiField[] fields = psiClass.getFields();
                for (PsiField field : fields) {
                    Object fieldDefaultValue  = PsiClassHelper.getJavaBaseTypeDefaultValue(field.getType().getPresentableText());
                    if(fieldDefaultValue != null)
                        baseTypeParamMap.put(field.getName(), fieldDefaultValue);
                }
            }
        }
        return baseTypeParamMap;
    }

    /* 基础类型默认值 */
    @Nullable
    public Map<String,Object>  getJavaBaseTypeDefaultValue(String paramName, String paramType) {
        Map<String,Object> paramMap = new LinkedHashMap<>();
        Object paramValue = null;
        paramValue = PsiClassHelper.getJavaBaseTypeDefaultValue(paramType);
        if (paramValue != null) {
            paramMap.put(paramType, paramValue);
        }
        return paramMap;
    }

    @NotNull
    public List<Parameter> getParameterList() {
        List<Parameter> parameterList = new ArrayList<>();

        PsiParameterList psiParameterList = psiMethod.getParameterList();
        PsiParameter[] psiParameters = psiParameterList.getParameters();
        for (PsiParameter psiParameter : psiParameters) {
            //忽略 request response

            String paramType = psiParameter.getType().getCanonicalText();
            if(paramType.equals("javax.servlet.http.HttpServletRequest")
                    || paramType.equals("javax.servlet.http.HttpServletResponse"))
                continue;
            //必传参数 @RequestParam
            PsiModifierList modifierList = psiParameter.getModifierList();
            boolean requestBodyFound = modifierList.findAnnotation(REQUEST_BODY.getQualifiedName()) != null;
            // 没有 RequestParam 注解, 有注解使用注解value
            String paramName = psiParameter.getName();
            String requestName = null;


            PsiAnnotation pathVariableAnno = modifierList.findAnnotation(PATH_VARIABLE.getQualifiedName());
            if (pathVariableAnno != null) {
                requestName = getAnnotationValue(pathVariableAnno);
                Parameter parameter = new Parameter(paramType, requestName != null ? requestName: paramName).setRequired(true).requestBodyFound(requestBodyFound);
                parameterList.add(parameter);
            }

            PsiAnnotation requestParamAnno = modifierList.findAnnotation(REQUEST_PARAM.getQualifiedName());
            if (requestParamAnno != null) {
                requestName = getAnnotationValue(requestParamAnno);
                Parameter parameter = new Parameter(paramType, requestName != null ? requestName: paramName).setRequired(true).requestBodyFound(requestBodyFound);
                parameterList.add(parameter);
            }

            if (pathVariableAnno == null && requestParamAnno == null) {
                Parameter parameter = new Parameter(paramType, paramName).requestBodyFound(requestBodyFound);
                parameterList.add(parameter);
            }
        }
        return parameterList;
    }

    public String getAnnotationValue(PsiAnnotation annotation) {
        String paramName = null;
        PsiAnnotationMemberValue attributeValue = annotation.findDeclaredAttributeValue("value");

        if (attributeValue != null && attributeValue instanceof PsiLiteralExpression) {
            paramName = (String) ((PsiLiteralExpression) attributeValue).getValue();
        }
        return paramName;
    }

    /**
     * 构建RequestBody json 参数
     * @param parameter
     * @return
     */
    public String buildRequestBodyJson(Parameter parameter) {
//        JavaFullClassNameIndex.getInstance();

        Project project = psiMethod.getProject();
        final String  className = parameter.getParamType();

        String queryJson = PsiClassHelper.create(psiMethod.getContainingClass()).withModule(myModule).convertClassToJSON(className, project);
        return queryJson;
    }



    public String buildRequestBodyJson() {
        List<Parameter> parameterList = this.getParameterList();
        for (Parameter parameter : parameterList) {
            if (parameter.isRequestBodyFound()) {
                return buildRequestBodyJson(parameter);
            }
        }
        return null;
    }

    @NotNull
    public static String buildServiceUriPath(PsiMethod psiMethod) {
        String ctrlPath = null;
        String methodPath = null;

        //判断rest服务提供方式 spring or jaxrs
        PsiClass containingClass = psiMethod.getContainingClass();
        RestSupportedAnnotationHelper annotationHelper;
        if (isSpringRestSupported(containingClass)) {
            ctrlPath = RequestMappingAnnotationHelper.getOneRequestMappingPath(containingClass);
            methodPath = RequestMappingAnnotationHelper.getOneRequestMappingPath(psiMethod);
        }else if(isJaxrsRestSupported(containingClass)){
            ctrlPath = JaxrsAnnotationHelper.getClassUriPath(containingClass);
            methodPath = JaxrsAnnotationHelper.getMethodUriPath(psiMethod);
        }

        if (ctrlPath == null) {
            return null;
        }

        if (!ctrlPath.startsWith("/")) ctrlPath = "/".concat(ctrlPath);
        if (!ctrlPath.endsWith("/")) ctrlPath = ctrlPath.concat("/");
        if (methodPath.startsWith("/")) methodPath = methodPath.substring(1, methodPath.length());

        return ctrlPath + methodPath;
    }

    @NotNull
    public static String buildServiceUriPathWithParams(PsiMethod psiMethod) {
        String serviceUriPath = buildServiceUriPath(psiMethod);

        String params = PsiMethodHelper.create(psiMethod).buildParamString();
        // RequestMapping 注解设置了 param
        if (!params.isEmpty()) {
            StringBuilder urlBuilder = new StringBuilder(serviceUriPath);
            return urlBuilder.append(serviceUriPath.contains("?") ? "&": "?").append(params).toString();
        }
        return  serviceUriPath;
    }

    //包含 "RestController" "Controller"
    public static boolean isSpringRestSupported(PsiClass containingClass) {
        PsiModifierList modifierList = containingClass.getModifierList();

        /*return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null ;*/

        return modifierList.findAnnotation(SpringControllerAnnotation.REST_CONTROLLER.getQualifiedName()) != null ||
                modifierList.findAnnotation(SpringControllerAnnotation.CONTROLLER.getQualifiedName()) != null ;
    }

    //包含 "RestController" "Controller"
    public static boolean isJaxrsRestSupported(PsiClass containingClass) {
        PsiModifierList modifierList = containingClass.getModifierList();

        return modifierList.findAnnotation(JaxrsRequestAnnotation.PATH.getQualifiedName()) != null ;
    }



}
