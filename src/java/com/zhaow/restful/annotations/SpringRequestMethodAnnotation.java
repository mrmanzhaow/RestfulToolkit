package com.zhaow.restful.annotations;


public enum SpringRequestMethodAnnotation {

    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping", null),
    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping", "GET"),
    POST_MAPPING( "org.springframework.web.bind.annotation.PostMapping", "POST"),
    PUT_MAPPING( "org.springframework.web.bind.annotation.PutMapping", "PUT"),
    DELETE_MAPPING( "org.springframework.web.bind.annotation.DeleteMapping", "DELETE"),
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping", "PATCH");

    SpringRequestMethodAnnotation(String qualifiedName, String methodName) {
        this.qualifiedName = qualifiedName;
        this.methodName = methodName;
    }

    private String qualifiedName;
    private String methodName;

   public String methodName() {
        return this.methodName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getShortName() {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".")-1);
    }

    public static SpringRequestMethodAnnotation getByQualifiedName(String qualifiedName) {
        for (SpringRequestMethodAnnotation springRequestAnnotation : SpringRequestMethodAnnotation.values()) {
            if (springRequestAnnotation.getQualifiedName().equals(qualifiedName)) {
                return springRequestAnnotation;
            }
        }
       return null;
    }

}