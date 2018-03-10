package com.zhaow.restful.navigation.action;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.gotoByName.CustomMatcherModel;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.zhaow.restful.common.spring.AntPathMatcher;
import com.zhaow.restful.method.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

/**
 * Model for "Go to | File" action
 */
public class GotoRequestMappingModel extends FilteringGotoByModel<HttpMethod> implements DumbAware, CustomMatcherModel {

    protected GotoRequestMappingModel(@NotNull Project project, @NotNull ChooseByNameContributor[] contributors) {
        super(project, contributors);
    }
// 设置过滤项
/*    @Override
    public synchronized void setFilterItems(Collection<HttpMethod> filterItems) {
        super.setFilterItems(filterItems);
    }*/

    // TODO: 过滤模块？ FilteringGotoByModel.acceptItem 调用，结合 重写 setFilterItems或 getFilterItems() 实现，可过滤模块 或者 method (如GotoClassModel2过滤language，重写 getFilterItems())
    @Nullable
    @Override
    protected HttpMethod filterValueFor(NavigationItem item) {
        if (item instanceof RestServiceItem) {
//            if (((RestServiceItem) item).getModule().getName().contains("eureka")) {
                return ((RestServiceItem) item).getMethod();
//            }
        }

        return null;
    }

    /* 可选项 */
    @Nullable
    @Override
    protected synchronized Collection<HttpMethod> getFilterItems() {
        /*final Collection<Language> result = super.getFilterItems();
        if (result == null) {
            return null;
        }
        final Collection<Language> items = new HashSet<>(result);
        items.add(Language.ANY);
        return items;*/

/*        ArrayList items = new ArrayList();
        items.add(HttpMethod.POST);
        return items;*/
        return super.getFilterItems();

    }

    @Override
    public String getPromptText() {
        return "Enter service URL path :";
    }

    @Override
    public String getNotInMessage() {
       return IdeBundle.message("label.no.matches.found.in.project");
//        return "No matched method found";
    }

    @Override
    public String getNotFoundMessage() {
       return IdeBundle.message("label.no.matches.found");
//        return "Service path not found";
    }

    @Override
    public char getCheckBoxMnemonic() {
        return SystemInfo.isMac?'P':'n';
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(myProject);
        return propertiesComponent.isTrueValue("GoToRestService.OnlyCurrentModule");
    }

    /* 选择 item 跳转触发 */
    @Override
    public void saveInitialCheckBoxState(boolean state) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(myProject);
        if (propertiesComponent.isTrueValue("GoToRestService.OnlyCurrentModule")) {
            propertiesComponent.setValue("GoToRestService.OnlyCurrentModule", Boolean.toString(state));
        }
    }

    @Nullable
    @Override
    public String getFullName(Object element) {
        return getElementName(element);
    }

    // 截取 Separators 后面pattern
    @NotNull
    @Override
    public String[] getSeparators() {
//        return new String[]{":","?"};
        return new String[]{"/","?"};
    }


    /** return null to hide checkbox panel */
    @Nullable
    @Override
    public String getCheckBoxName() {
        return "Only This Module";
//        return null;
    }


    @Override
    public boolean willOpenEditor() {
        return true;
    }

//    CustomMatcherModel 接口，Allows to implement custom matcher for matching items from ChooseByName popup
    // todo: resolve PathVariable annotation
    @Override
    public boolean matches(@NotNull String popupItem, @NotNull String userPattern) {
        String pattern = userPattern;
        if(pattern.equals("/")) return true;
        // REST风格的参数  @RequestMapping(value="{departmentId}/employees/{employeeId}")  PathVariable
        // REST风格的参数（正则） @RequestMapping(value="/{textualPart:[a-z-]+}.{numericPart:[\\d]+}")  PathVariable

//        pattern = StringUtils.removeRedundancyMarkup(pattern);

//        userPattern  输入的过滤文字
//        DefaultChooseByNameItemProvider.buildPatternMatcher
        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + pattern, NameUtil.MatchingCaseSensitivity.NONE);
        boolean matches = matcher.matches(popupItem);
        if (!matches) {
            AntPathMatcher pathMatcher = new AntPathMatcher();
            matches = pathMatcher.match(popupItem,userPattern);
        }
        return matches;
//        return true;

    }

// 没用 ？
    @NotNull
    @Override
    public String removeModelSpecificMarkup(@NotNull String pattern) {
        return super.removeModelSpecificMarkup(pattern);
//        return "demo";
    }

    /* TODO :重写渲染*/
    @Override
    public ListCellRenderer getListCellRenderer() {

        return super.getListCellRenderer();
    }


}
