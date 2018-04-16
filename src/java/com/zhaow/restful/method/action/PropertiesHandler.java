package com.zhaow.restful.method.action;


import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
// profile.active != null  // YamlPropertySourceLoader extends PropertySourceLoader .load

// PropertySourcesLoader.load 配置文件加载类
// 路径location：[file:./config/, file:./, classpath:/config/, classpath:/]
//文件name：bootstrap，application
//后缀：[properties, xml, yml, yaml]
//applicationConfig: [classpath:/application.yml]#prod

// 如果存在 activeProfile spring.profiles.active 存在，判断是否存在 application-activeProfile. 文件，
// 如果存在，判断是否存在设置，不存在则忽略
//最终可能优先级 application.properties>application.yml>bootstrap.propertis>bootstrap.yml
//路径：classpath:/(resource)>classpath:/config/
public class PropertiesHandler {

    public String[] getFileExtensions() { //优先级
        return new String[]{"properties", "yml"};
    }

    public String[] getConfigFiles() { // 优先级
        return new String[]{"application", "bootstrap"};
    }
    public List<String > CONFIG_FILES = Arrays.asList("application", "bootstrap");
    public List<String > FILE_EXTENSIONS = Arrays.asList("properties", "yml");

    String SPRING_PROFILE = "spring.profiles.active";

    String placeholderPrefix = "${";
    String valueSeparator = ":";
    String placeholderSuffix = "}";

    String activeProfile;

    Module module;

    public PropertiesHandler(Module module) {
        this.module = module;
    }

    public String getServerPort() {
        String port = null;
        String serverPortKey = "server.port";

        activeProfile = findProfilePropertyValue();

        //
        if (activeProfile != null) {
            port = findPropertyValue(serverPortKey, activeProfile);
        }
        if (port == null) {
            port = findPropertyValue(serverPortKey, null);
        }

        return port != null ? port : "";
    }

    public String getProperty(String propertyKey) {
        String propertyValue = null;

        activeProfile = findProfilePropertyValue();

        //
        if (activeProfile != null) {
            propertyValue = findPropertyValue(propertyKey, activeProfile);
        }
        if (propertyValue == null) {
            propertyValue = findPropertyValue(propertyKey, null);
        }

        return propertyValue != null ? propertyValue : "";
    }



    /* try find spring.profiles.active value */
    private String findProfilePropertyValue() {
        String activeProfile = findPropertyValue(SPRING_PROFILE, null);
        return activeProfile;
    }

    /* 暂时不考虑路径问题，默认找到的第一文件 */
    private String findPropertyValue(String propertyKey,String activeProfile) {
        String value = null;
        String profile = activeProfile != null ? "-"+ activeProfile : "";
        //
        for (String conf : getConfigFiles()) {
            for (String ext : getFileExtensions()) {
                // load spring config file
                String configFile = conf + profile + "." +  ext;
                if (ext.equals("properties")) {
                    Properties properties = loadProertiesFromConfigFile(configFile);
                    if (properties != null) {
                        Object valueObj = properties.getProperty(propertyKey);
                        if (valueObj != null) {
                            value = cleanPlaceholderIfExist((String)valueObj);
                            return value;
                        }
                    }

                } else if(ext.equals("yml") || ext.equals("yaml")) {
                    Map<String, Object> propertiesMap = getPropertiesMapFromYamlFile(configFile);
                    if (propertiesMap != null) {
                        Object valueObj = propertiesMap.get(propertyKey);
                        if (valueObj == null) return null;

                        if (valueObj instanceof String) {
                            value = cleanPlaceholderIfExist((String)valueObj);
                        }else{
                            value = valueObj.toString();
                        }
                        return value;
                    }
                }
            }
        }

        return value;
    }

    private Properties loadProertiesFromConfigFile(String configFile) {
        Properties properties = null;
        PsiFile applicationPropertiesFile = findPsiFileInModule(configFile);
        if (applicationPropertiesFile != null) {
            properties = loadPropertiesFromText(applicationPropertiesFile.getText());
        }
        return properties;
    }

    @NotNull
    private Properties loadPropertiesFromText(String text) {
        Properties prop = new Properties();
        try {
            prop.load(new StringReader(text));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    public String getContextPath() {
        String key = "server.context-path";
        String contextPath = null;

        activeProfile = findProfilePropertyValue();

        //
        if (activeProfile != null) {
            contextPath = findPropertyValue(key, activeProfile);
        }
        if (contextPath == null) {
            contextPath = findPropertyValue(key, null);
        }

        return contextPath != null ? contextPath : "";

    }

    private String cleanPlaceholderIfExist(String value) {
        // server.port=${PORT:8080}
        if (value != null && value.contains(placeholderPrefix) && value.contains(valueSeparator)) {
            String[] split = value.split(valueSeparator);


            if (split.length > 1) {
                value = split[1].replace(placeholderSuffix, "");
            }
//            value = value.replace(placeholderPrefix,"").replace(placeholderSuffix,"");
        }
        return value;
    }


    private Map<String, Object> getPropertiesMapFromYamlFile(String configFile) {
        PsiFile applicationPropertiesFile = findPsiFileInModule(configFile);
        if (applicationPropertiesFile != null) {
            Yaml yaml = new Yaml();

            String yamlText = applicationPropertiesFile.getText();
            try {
                Map<String, Object> ymlPropertiesMap = (Map<String, Object>) yaml.load(yamlText);
                return getFlattenedMap(ymlPropertiesMap);
            } catch (Exception e) { // FIXME: spring 同一个文件中配置多个环境时； yaml 格式不规范，比如包含 “---“

                return null;
            }

//        Object yamlProperty = getYamlProperty(key, ymlPropertiesMap);
        }
        return null;
    }


    private PsiFile findPsiFileInModule(String fileName) {
        PsiFile psiFile = null;
        PsiFile[] applicationProperties = FilenameIndex.getFilesByName(module.getProject(),
                fileName,
                GlobalSearchScope.moduleScope(module));

        if (applicationProperties.length > 0) {
            psiFile = applicationProperties[0];
        }

        return psiFile;
    }

    /**
     * ref: org.springframework.beans.factory.config.YamlProcessor
     */
    protected final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap();
        this.buildFlattenedMap(result, source, null);
        return result;
    }

    private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        Iterator iterator = source.entrySet().iterator();

        while(true) {
            while(iterator.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)iterator.next();
                String key = entry.getKey();
                if (StringUtils.isNotBlank(path)) {
                    if (key.startsWith("[")) {
                        key = path + key;
                    } else {
                        key = path + '.' + key;
                    }
                }

                Object value = entry.getValue();
                if (value instanceof String) {
                    result.put(key, value);
                } else if (value instanceof Map) {
                    Map<String, Object> map = (Map)value;
                    this.buildFlattenedMap(result, map, key);
                } else if (value instanceof Collection) {
                    Collection<Object> collection = (Collection)value;
                    int count = 0;
                    Iterator var10 = collection.iterator();

                    while(var10.hasNext()) {
                        Object object = var10.next();
                        this.buildFlattenedMap(result, Collections.singletonMap("[" + count++ + "]", object), key);
                    }
                } else {
                    result.put(key, value != null ? value : "");
                }
            }

            return;
        }
    }

}
