package com.zhaow.restful.navigation.action;


import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.zhaow.restful.common.ServiceHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//
public class GotoRequestMappingContributor implements ChooseByNameContributor {
    Module myModule;

    private List<RestServiceItem> navItem;

    public GotoRequestMappingContributor() {
    }
    public GotoRequestMappingContributor(Module myModule) {
        this.myModule = myModule;
    }



    //Returns the list of names for the specified project to which it is possible to navigate by name. 所有该类型的文件列表
    @NotNull
    @Override
    public String[] getNames(Project project, boolean onlyThisModuleChecked) {
        String[] names = null;
        List<RestServiceItem> itemList;
        ///todo 查找 project 中所有符合 rest url 类型文件，包含 request 接口
        if (onlyThisModuleChecked && myModule != null) {
            itemList = findAllRestServiceItemList(myModule);
        } else {
            itemList = findAllRestServiceItemList(project);
        }

        navItem = itemList;

        if (itemList != null) names = new String[itemList.size()];

        for (int i = 0; i < itemList.size(); i++) {
            RestServiceItem requestMappingNavigationItem = itemList.get(i);
            names[i] = requestMappingNavigationItem.getName();
        }

        return names;
    }

    private List<RestServiceItem> findAllRestServiceItemList(Module myModule) {
        List<RestServiceItem> itemList = ServiceHelper.buildRestServiceItemList(myModule);

        return itemList;
    }

    private List<RestServiceItem> findAllRestServiceItemList(Project project) {
//        Arrays.stream(SpringRequestAnnotation.values()).flatMap(annotation -> )
        List<RestServiceItem> itemList = ServiceHelper.buildRestServiceItemList(project);

        return itemList;
    }

    //Returns the list of navigation items matching the specified name. 匹配，对比
    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean onlyThisModuleChecked) {
//        AntPathMatcher
        NavigationItem[] navigationItems = navItem.stream().filter(item -> item.getName().equals(name)).toArray(NavigationItem[]::new);
        return navigationItems;

    }
}
