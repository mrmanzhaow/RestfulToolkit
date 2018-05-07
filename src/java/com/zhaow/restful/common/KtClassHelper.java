package com.zhaow.restful.common;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.java.stubs.index.JavaShortClassNameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.caches.KotlinShortNamesCache;
import org.jetbrains.kotlin.idea.stubindex.KotlinClassShortNameIndex;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtClassOrObject;

import java.util.Collection;

// 处理 实体自关联，第二层自关联字段
public class KtClassHelper {
    KtClass psiClass;

    private static int  autoCorrelationCount=0; //标记实体递归
    private int listIterateCount = 0; //标记List递归
    private Module myModule;

    protected KtClassHelper(@NotNull KtClass psiClass) {
        this.psiClass = psiClass;
    }

    @NotNull
    protected Project getProject() {
        return psiClass.getProject();
    }

    @Nullable
    public KtClassOrObject findOnePsiClassByClassName(String className, Project project) {
        KtClassOrObject psiClass = null;

        String shortClassName = className.substring(className.lastIndexOf(".") + 1, className.length());

        PsiClass[] classesByName = KotlinShortNamesCache.getInstance(project).getClassesByName(shortClassName, GlobalSearchScope.allScope(project));
        Collection<PsiClass> psiClassCollection = JavaShortClassNameIndex.getInstance().get(shortClassName, project, GlobalSearchScope.projectScope(project));
        Collection<KtClassOrObject> ktClassOrObjectss = KotlinClassShortNameIndex.getInstance().get(shortClassName, project, GlobalSearchScope.allScope(project));
        ////////////

//        Collection<PsiClass> psiClassCollection = tryDetectPsiClassByShortClassName(project, shortClassName);
        Collection<KtClassOrObject> ktClassOrObjects = tryDetectPsiClassByShortClassName(project, shortClassName);
        if (ktClassOrObjects.size() == 0) {

            return null;
        }
        if (ktClassOrObjects.size() == 1) {
            psiClass = ktClassOrObjects.iterator().next();
        }
        psiClass.getPrimaryConstructor().getText(); // (val id: Long, val content: String)
        psiClass.getFqName(); // class fullQualifiedName :org.jetbrains.kotlin.demo.Greeting

        if (ktClassOrObjects.size() > 1) {
            //找import中对应的class
//            psiClass = psiClassCollection.stream().filter(tempKtClass -> tempPsiClass.getQualifiedName().equals(className)).findFirst().get();

            /*Optional<PsiClass> any = psiClassCollection.stream().filter(tempPsiClass -> tempPsiClass.getQualifiedName().equals(className)).findAny();

            if (any.isPresent()) {
                psiClass = any.get();
            }*/

            for (KtClassOrObject ktClassOrObject : ktClassOrObjects) {
//                ktClassOrObject.
            }

        }
        return psiClass;
    }


//PsiShortNamesCache : PsiClass:Demo    KtLightClassImpl:data class Greeting(val id: Long, val content: String) { 代码体 }
    public Collection<KtClassOrObject> tryDetectPsiClassByShortClassName(Project project, String shortClassName) {
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(shortClassName, GlobalSearchScope.allScope(project));// 所有的
        PsiClass[] classesByName = KotlinShortNamesCache.getInstance(project).getClassesByName(shortClassName, GlobalSearchScope.allScope(project));
//        Collection<PsiClass> psiClassCollection = JavaShortClassNameIndex.getInstance().get(shortClassName, project, GlobalSearchScope.projectScope(project));
        Collection<KtClassOrObject> ktClassOrObjects = KotlinClassShortNameIndex.getInstance().get(shortClassName, project, GlobalSearchScope.allScope(project));
//        ((KtLightClassImpl) classesByName[0]).getKotlinOrigin()   ; classesByName[0] instanceof KtLightClass
        for (KtClassOrObject ktClassOrObject : ktClassOrObjects) {
//            ktClassOrObject
        }
        if (ktClassOrObjects.size() > 0) {
            return ktClassOrObjects;
        }
        if(myModule != null) {
            ktClassOrObjects = KotlinClassShortNameIndex.getInstance().get(shortClassName, project, GlobalSearchScope.allScope(project));
        }
        return ktClassOrObjects;
    }

    public static KtClassHelper create(@NotNull KtClass psiClass) {
        return new KtClassHelper(psiClass);
    }


}