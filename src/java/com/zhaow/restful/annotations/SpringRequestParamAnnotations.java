package com.zhaow.restful.annotations;


public enum SpringRequestParamAnnotations {
    REQUEST_PARAM("RequestParam", "org.springframework.web.bind.annotation.RequestParam"),
    REQUEST_BODY("RequestBody", "org.springframework.web.bind.annotation.RequestBody"),
    PATH_VARIABLE("PathVariable", "org.springframework.web.bind.annotation.PathVariable");

    SpringRequestParamAnnotations(String shortName, String qualifiedName) {
        this.shortName = shortName;
        this.qualifiedName = qualifiedName;
    }

    private String shortName;
    private String qualifiedName;

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getShortName() {
        return shortName;
    }

}