package com.zhaow.restful.common;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.zhaow.restful.annotations.JaxrsRequestAnnotation;
import com.zhaow.restful.annotations.SpringControllerAnnotation;
import com.zhaow.restful.common.jaxrs.JaxrsAnnotationHelper;
import com.zhaow.restful.common.spring.RequestMappingAnnotationHelper;
import com.zhaow.restful.method.Parameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.asJava.LightClassUtilsKt;
import org.jetbrains.kotlin.psi.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.zhaow.restful.annotations.SpringRequestParamAnnotations.*;

/**
 * KtFunction处理类
 */
public class KtFunctionHelper extends PsiMethodHelper {
    KtNamedFunction ktNamedFunction;
    Project myProject;
    Module myModule;

    private String pathSeparator= "/";

    public static KtFunctionHelper create(@NotNull KtNamedFunction psiMethod) {
        return new KtFunctionHelper(psiMethod);
    }

    public KtFunctionHelper withModule(Module module) {
        this.myModule = module;
        return this;
    }

    protected KtFunctionHelper(@NotNull KtNamedFunction ktNamedFunction) {
        super(null);
        List<PsiMethod> psiMethods = LightClassUtilsKt.toLightMethods(ktNamedFunction);
        PsiMethod psiMethod = psiMethods.get(0);
        super.psiMethod = psiMethod;
        this.ktNamedFunction = ktNamedFunction;
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
    /*@NotNull
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
            Object defaultValue = KtClassHelper.getJavaBaseTypeDefaultValue(shortTypeName);
            //简单常用类型
            if (defaultValue != null) {
                baseTypeParamMap.put(parameter.getParamName(),(defaultValue));
                continue;
            }

            KtClassHelper psiClassHelper = KtClassHelper.create((KtClass) psiMethod.getParent().getParent());
            KtClassOrObject ktClass = psiClassHelper.findOnePsiClassByClassName(parameter.getParamType(), getProject());
            PsiClass psiClass = psiClassHelper.findOnePsiClassByClassName2(parameter.getParamType(), getProject());
            if (psiClass != null) {
                PsiField[] fields = psiClass.getFields();
                for (PsiField field : fields) {
                    Object fieldDefaultValue  = PsiClassHelper.getJavaBaseTypeDefaultValue(field.getType().getPresentableText());
                    if(fieldDefaultValue != null)
                        baseTypeParamMap.put(field.getName(), fieldDefaultValue);
                }
            }
*//*
            if (ktClass != null) {
                List<KtParameter> ktParameters = ktClass.getPrimaryConstructorParameters();
                for (KtParameter ktParameter : ktParameters) {
                    Object typeDefaultValue = KtClassHelper.getJavaBaseTypeDefaultValue(ktParameter.getTypeReference().getText());
                    if(typeDefaultValue != null)
                        baseTypeParamMap.put(ktParameter.getName(), typeDefaultValue);

                }
            }*//*

           *//* if (ktClass instanceof KtClass) {
                List<KtProperty> ktProperties = ((KtClass) ktClass).getProperties();
                for (KtProperty ktProperty : ktProperties) {
                    System.out.println(ktProperty);
//                    Object fieldDefaultValue  = KtClassHelper.getJavaBaseTypeDefaultValue(ktProperty.getTypeReference()getPresentableText());
                    if(fieldDefaultValue != null)
                        baseTypeParamMap.put(ktProperty.getName(), fieldDefaultValue);
                }
            }*//*
        }
        return baseTypeParamMap;
    }*/



}
