package com.zhaow.restful.annotations;


public enum SpringRequestAnnotation {

    REQUEST_MAPPING("RequestMapping", "org.springframework.web.bind.annotation.RequestMapping", null),
    GET_MAPPING("GetMapping", "org.springframework.web.bind.annotation.GetMapping", "GET"),
    POST_MAPPING("PostMapping", "org.springframework.web.bind.annotation.PostMapping", "POST"),
    PUT_MAPPING("PutMapping", "org.springframework.web.bind.annotation.PutMapping", "PUT"),
    DELETE_MAPPING("DeleteMapping", "org.springframework.web.bind.annotation.DeleteMapping", "DELETE"),
    PATCH_MAPPING("PatchMapping", "org.springframework.web.bind.annotation.PatchMapping", "PATCH");

    SpringRequestAnnotation(String shortName, String qualifiedName, String methodName) {
        this.shortName = shortName;
        this.qualifiedName = qualifiedName;
        this.methodName = methodName;
    }

    private String shortName;
    private String qualifiedName;
    private String methodName;

   public String methodName() {
        return this.methodName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getShortName() {
        return shortName;
    }

    public static SpringRequestAnnotation getByShortName(String shortName) {
        for (SpringRequestAnnotation springRequestAnnotation : SpringRequestAnnotation.values()) {
            if (springRequestAnnotation.getShortName().equals(shortName)) {
                return springRequestAnnotation;
            }
        }
        return null;
    }

    public static SpringRequestAnnotation getByQualifiedName(String qualifiedName) {
        for (SpringRequestAnnotation springRequestAnnotation : SpringRequestAnnotation.values()) {
            if (springRequestAnnotation.getQualifiedName().equals(qualifiedName)) {
                return springRequestAnnotation;
            }
        }
       return null;
    }

}