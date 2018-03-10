package com.zhaow.restful.navigator;


import com.intellij.openapi.module.Module;
import com.zhaow.restful.navigation.action.RestServiceItem;

import java.util.List;

public class RestServiceProject {
    String port = "8080";
    String appName;

    String moduleName;
    Module module;

    String applicationClass;

    List<RestServiceItem> serviceItems;

    public RestServiceProject() {
        appName = "demoAppName";
        moduleName = "demoModuleName";
    }

    public RestServiceProject(String moduleName, List<RestServiceItem> serviceItems) {
        this.moduleName = moduleName;
        port = port;
        appName = moduleName;
        this.serviceItems = serviceItems;
    }

    public RestServiceProject(Module module, List<RestServiceItem> serviceItems) {
        this.moduleName = module.getName();
        port = port;
        appName = moduleName;
        this.serviceItems = serviceItems;
    }

    // service list

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getApplicationClass() {
        return applicationClass;
    }

    public void setApplicationClass(String applicationClass) {
        this.applicationClass = applicationClass;
    }

    @Override
    public String toString() {
        return appName + ":" + port;
    }
}
