package com.zhaow.restful.annotations;


public enum SpringControllerAnnotation implements PathMappingAnnotation {

    CONTROLLER("Controller", "org.springframework.stereotype.Controller"),
    REST_CONTROLLER("RestController", "org.springframework.web.bind.annotation.RestController");

    SpringControllerAnnotation(String shortName, String qualifiedName) {
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

/*
    @Override
    public List<PathMappingAnnotation> getPathMappings() {
        return allPathMappingAnnotations;
    }
*/

/*    static {
        for (SpringControllerAnnotation springControllerAnnotation : SpringControllerAnnotation.values()) {
            allPathMappingAnnotations.add(springControllerAnnotation);
        }
    }*/

}