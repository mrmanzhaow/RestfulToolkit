package com.zhaow.restful.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.java.stubs.index.JavaShortClassNameIndex;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.text.DateFormatUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.*;
// 处理 实体自关联，第二层自关联字段
public class PsiClassHelper {
    PsiClass psiClass;

    private static int  autoCorrelationCount=0; //标记实体递归
    private int listIterateCount = 0; //标记List递归
    private Module myModule;

    protected PsiClassHelper(@NotNull PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    @NotNull
    protected Project getProject() {
        return psiClass.getProject();
    }


    public String convertClassToJSON(String className, Project project) {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        String queryJson;
        if (className.contains("List<")) { //参数为 List
            List<Map<String,Object>> jsonList = new ArrayList<>();

            // 没处理泛型嵌套，
            String entityName = className.substring(className.indexOf("<")+1,className.lastIndexOf(">"));

            // build RequestBody Json
            Map<String, Object> jsonMap = assembleClassToMap(entityName, project);
            jsonList.add(jsonMap);
            queryJson = gson.toJson(jsonList);
        } else {
            // build RequestBody Json
            queryJson = convertPojoEntityToJSON(className, project);
        }
        return queryJson;
    }

    private String convertPojoEntityToJSON(String className, Project project) {
        String queryJson;
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson gson = gsonBuilder.create() ;
        Map<String, Object> jsonMap = assembleClassToMap(className, project);
        queryJson =  gson.toJson(jsonMap) ;
        return queryJson;
    }


    public String convertClassToJSON(Project project , boolean prettyFormat) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        if(prettyFormat) gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create() ;
        Map<String, Object> jsonMap = new HashMap<>();

        if(psiClass != null){
            jsonMap = assembleClassToMap(psiClass, project);
        }
        String queryJson =  gson.toJson(jsonMap) ;
        return queryJson;
    }


    @Nullable
    public static Object getJavaBaseTypeDefaultValue(String paramType) {
//        Map<String,Object> paramMap = new LinkedHashMap<>();
        Object paramValue = null;
        switch (paramType.toLowerCase()) {
            case "byte": paramValue = Byte.valueOf("1");break;
            case "char": paramValue = Character.valueOf('Z');break;
            case "character": paramValue = Character.valueOf('Z');break;
            case "boolean": paramValue = Boolean.TRUE;break;
            case "int": paramValue = Integer.valueOf(1);break;
            case "integer": paramValue = Integer.valueOf(1);break;
            case "double": paramValue = Double.valueOf(1);break;
            case "float": paramValue = Float.valueOf(1.0F);break;
            case "long": paramValue = Long.valueOf(1L);break;
            case "short": paramValue = Short.valueOf("1");break;
            case "string": paramValue = "demoData";break;
            case "date": paramValue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) ;break; // todo: format date
//            default: paramValue = paramType;
        }
//
//        if (paramValue != null) {
//            paramMap.put(paramType, paramValue);
//        }
        return paramValue;
    }

    @Nullable
    public PsiClass findOnePsiClassByClassName(String className, Project project) {
        PsiClass psiClass = null;

        String shortClassName = className.substring(className.lastIndexOf(".") + 1, className.length());
        Collection<PsiClass> psiClassCollection = tryDetectPsiClassByShortClassName(project, shortClassName);
        if (psiClassCollection.size() == 0) {

            return null;
        }
        if (psiClassCollection.size() == 1) {
            psiClass = psiClassCollection.iterator().next();
        }

        if (psiClassCollection.size() > 1) {
            //找import中对应的class
            psiClass = psiClassCollection.stream().filter(tempPsiClass -> tempPsiClass.getQualifiedName().equals(className)).findFirst().get();

            Optional<PsiClass> any = psiClassCollection.stream().filter(tempPsiClass -> tempPsiClass.getQualifiedName().equals(className)).findAny();

            if (any.isPresent()) {
                psiClass = any.get();
            }

        }
        return psiClass;
    }

    public Collection<PsiClass> tryDetectPsiClassByShortClassName(Project project, String shortClassName) {
        Collection<PsiClass> psiClassCollection = JavaShortClassNameIndex.getInstance().get(shortClassName, project, GlobalSearchScope.projectScope(project));

        if (psiClassCollection.size() > 0) {
            return psiClassCollection;
        }

        if(myModule != null) {
            psiClassCollection = JavaShortClassNameIndex.getInstance().get(shortClassName, project, GlobalSearchScope.allScope(project));
        }

        return psiClassCollection;
    }

    public static PsiClassHelper create(@NotNull PsiClass psiClass) {
        return new PsiClassHelper(psiClass);
    }

    public Map<String, Object> assembleClassToMap(String className, Project project) {
        PsiClass psiClass = findOnePsiClassByClassName(className, project);

        Map<String, Object> jsonMap = new HashMap<>();
        if(psiClass != null){
            jsonMap = assembleClassToMap(psiClass, project);
        }
        return jsonMap;
    }

    public Map<String, Object> assembleClassToMap(PsiClass psiClass, Project project) {
        Map<String, Object> map = new LinkedHashMap<>();
        PsiField[] fields = psiClass.getFields();
        for (PsiField field : fields) {
            PsiType psiFieldType = field.getType();
            psiFieldType.getPresentableText();

//   todo:         判断 list 递归
            if (isListFieldType(psiFieldType)) {
                PsiType[] parameters = ((PsiClassReferenceType) psiFieldType).getParameters();
                if (parameters != null && parameters.length > 0) {
                    PsiType parameter = parameters[0];
                    if (parameter.getPresentableText().equals(psiClass.getName())) {
                        continue;
                    } else {
                        // TODO TODO TODO .................
                        if (parameter instanceof PsiClassReferenceType) {
//                            (PsiClassReferenceType) parameter
                        }
                    }
                }
            }

            Object fieldValueObj = setFieldDefaultValue(psiFieldType, project);
            map.put(field.getName(), fieldValueObj);
        }

        return map;
    }


    private Object setFieldDefaultValue(PsiType psiFieldType, Project project) {

        // 八种类型和包装类
        String typeName = psiFieldType.getPresentableText();

        switch (typeName.toLowerCase()) {
            case "byte": return Byte.valueOf("1");
            case "char": return Character.valueOf('Z');
            case "character": return Character.valueOf('Z');
            case "boolean": return Boolean.TRUE;
            case "int": return Integer.valueOf(1);
            case "integer": return Integer.valueOf(1);
            case "double": return Double.valueOf(1);
            case "float": return Float.valueOf(1.0F);
            case "long": return Long.valueOf(1L);
            case "short": return Short.valueOf("1");
            case "string": return "demoData";
            case "date": return DateFormatUtil.formatDateTime(new Date());
//            default:
        }


        if (psiFieldType instanceof PsiClassReferenceType) {
            String className = ((PsiClassReferenceType) psiFieldType).getClassName();
//            PsiUtil.getTopLevelClass(psiFieldType);
            if (className.equalsIgnoreCase("List") || className.equalsIgnoreCase("ArrayList")) {

                PsiType[] parameters = ((PsiClassReferenceType) psiFieldType).getParameters();
                if (parameters != null && parameters.length > 0) {
                    PsiType parameter = parameters[0];
//                    if(parameter.getPresentableText().equals(psiclass.getname))
                }

                return handleListParam(psiFieldType, project);
            }

            String fullName = psiFieldType.getCanonicalText();
            PsiClass fieldClass = findOnePsiClassByClassName(fullName, project);

            // 处理递归
            if (fieldClass != null) {
//                todo: 处理递归问题 autoCorrelationCount
                if(autoCorrelationCount > 0) return new HashMap();
                if(fullName.equals(fieldClass.getQualifiedName())){
                    autoCorrelationCount ++;
                }

                return assembleClassToMap(fieldClass, project);
            }
        }

        // 处理自关联 List<T> = T 两级，只处理一次, break

        return typeName;
    }

    /* 字段是否为List 类型*/
    private static boolean isListFieldType(PsiType psiFieldType) {
        if (! (psiFieldType instanceof PsiClassReferenceType)) {
            return false;
        }

        String className = ((PsiClassReferenceType) psiFieldType).getClassName();
        return  className.equalsIgnoreCase("List") || className.equalsIgnoreCase("ArrayList") ;
    }

    private Object handleListParam(PsiType psiType, Project project) {
        List<Object> list = new ArrayList();
        PsiClassType classType = (PsiClassType) psiType;
        PsiType[] subTypes = classType.getParameters();
        if (subTypes.length > 0) {
            PsiType subType = subTypes[0];
//            listIterateCount++;

            list.add(setFieldDefaultValue(subType, project));

//            String subTypeName = subType.getCanonicalText();
//            if (subTypeName.startsWith("List")) {
//                list.add(handleListParam(subType, project));
//            } else {
//                PsiClass targetClass = findOnePsiClassByClassName(subTypeName, project);
//                if (targetClass != null) {
//                    list.add(assembleClassToMap(targetClass, project));
//                } else if (subTypeName.equals("String")) {
//                    list.add("str");
//                } else if (subTypeName.equals("Date")) {
//                    list.add(Long.valueOf(new Date().getTime()));
//                } else {
//                    list.add(subTypeName);
//                }
//            }
        }
        return list;
    }


    public PsiClassHelper withModule(Module module) {
        this.myModule = module;
        return this;
    }
}