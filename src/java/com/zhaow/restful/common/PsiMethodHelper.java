package com.zhaow.restful.common;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.zhaow.restful.annotations.SpringRequestAnnotation;
import com.zhaow.restful.method.Parameter;
import com.zhaow.restful.method.RequestMapping;
import com.zhaow.restful.method.action.RequestMappingAnnotationHelper;
import com.zhaow.restful.navigation.action.RestServiceItem;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.zhaow.restful.annotations.SpringRequestParamAnnotations.*;

/**
 * PsiMethod处理类
 */
public class PsiMethodHelper {
    PsiMethod psiMethod;
    Project myProject;
    Module myModule;


    private static final String REQUEST_METHOD_GET = "RequestMethod.GET";
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

    private boolean matchGetMethod() {
        PsiModifierList modifierList = psiMethod.getModifierList();
        // GetMapping
        PsiAnnotation getMappingAnnotation = modifierList.findAnnotation(SpringRequestAnnotation.GET_MAPPING.getQualifiedName());
        if (getMappingAnnotation != null) {
            return true;
        }
        // RequestMapping.method == null or 'GET'
        PsiAnnotation reqMappingAnnotation = modifierList.findAnnotation(SpringRequestAnnotation.REQUEST_MAPPING.getQualifiedName());
        if (reqMappingAnnotation == null) {
            return false;
        }
        PsiAnnotationMemberValue requestMethod = reqMappingAnnotation.findDeclaredAttributeValue("method");
        if( (requestMethod != null && requestMethod.equals(REQUEST_METHOD_GET)) ){
            return true;
        }
        return false;
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
            String paramType = parameter.getShortTypeName();
            Object defaultValue = PsiClassHelper.getJavaBaseTypeDefaultValue(paramType);
            //简单常用类型
            if (defaultValue != null) {
                baseTypeParamMap.put(parameter.getParamName(),(defaultValue));
                continue;
            }

            PsiClassHelper psiClassHelper = PsiClassHelper.create(psiMethod.getContainingClass());
            PsiClass psiClass = psiClassHelper.findOnePsiClassByClassName(paramType, getProject());
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


    //Note: 当Controller 类上没有标记@RequestMapping 注解时，方法上的@RequestMapping 都是绝对路径。
    public List<RestServiceItem> getServiceItemList() {
        List<RestServiceItem> itemList = new ArrayList<>();

        RequestMapping[] requestMappings = RequestMappingAnnotationHelper.getRequestMappings(psiMethod);

        //TODO:  controller 没有设置requestMapping，默认“/”
        // TODO : controller 设置了(rest)controller 所有方法未指定 requestMapping，所有方法均匹配； 存在方法指定 requestMapping，则只解析这些方法；
        String[] controllerPaths = RequestMappingAnnotationHelper.getRequestMappingValues(psiMethod.getContainingClass());

        for (String controllerPath : controllerPaths) {
            for (RequestMapping requestMapping : requestMappings) {
                String methodPath = requestMapping.getPath();

                String requestPath;
                if (!controllerPath.startsWith("/")) controllerPath = "/".concat(controllerPath);
                if (!controllerPath.endsWith("/")) controllerPath = controllerPath.concat("/");
                if (methodPath.startsWith("/")) methodPath = methodPath.substring(1, methodPath.length());
                requestPath = controllerPath + methodPath;

                RestServiceItem item = new RestServiceItem(psiMethod, requestMapping.getMethod(), requestPath);
                if (myModule != null) {
                    item.setModule(myModule);
                }

                itemList.add(item);
            }
        }

        return itemList;
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
    public static String buildServicePath(PsiMethod psiMethod) {
        String ctrlPath = getOneRequestMappingPath(psiMethod.getContainingClass());
        String methodPath = getOneRequestMappingPath(psiMethod);

        if (!ctrlPath.startsWith("/")) ctrlPath = "/".concat(ctrlPath);
        if (!ctrlPath.endsWith("/")) ctrlPath = ctrlPath.concat("/");
        if (methodPath.startsWith("/")) methodPath = methodPath.substring(1, methodPath.length());

        return ctrlPath + methodPath;
    }


    String concat(String path1, String path2) {
        if (StringUtils.isBlank(path1) && StringUtils.isBlank(path2)) {
            return "";
        }
        if (StringUtils.isBlank(path1)) {
            return path2;
        }
        if (StringUtils.isBlank(path2)) {
            return path1;
        }

        boolean path1EndsWithSeparator = path1.endsWith(this.pathSeparator);
        boolean path2StartsWithSeparator = path2.startsWith(this.pathSeparator);

        if (path1EndsWithSeparator && path2StartsWithSeparator) {
            return path1 + path2.substring(1);
        }
        else if (path1EndsWithSeparator || path2StartsWithSeparator) {
            return path1 + path2;
        }
        else {
            return path1 + this.pathSeparator + path2;
        }
    }

    public static String getOneRequestMappingPath(PsiMethod psiMethod) {
//        System.out.println("psiMethod:::::::" + psiMethod);
        SpringRequestAnnotation requestAnnotation = null;

        List<SpringRequestAnnotation> springRequestAnnotations = Arrays.stream(SpringRequestAnnotation.values()).filter(annotation ->
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


    public static String getOneRequestMappingPath(PsiClass psiClass) {
        // todo: 有必要 处理 PostMapping,GetMapping 么？
        PsiAnnotation annotation = psiClass.getModifierList().findAnnotation(SpringRequestAnnotation.REQUEST_MAPPING.getQualifiedName());

        String path = null;
        if (annotation != null) {
            path = RequestMappingAnnotationHelper.getRequestMappingValue(annotation);
        }

        return path != null ? path : "";
    }

}
