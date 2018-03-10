package com.zhaow.restful.annotations;


public enum JaxrsRequestParamAnnotation {
    QUERY_PARAM("QueryParam","javax.ws.rs.QueryParam"), PATH_PARAM("PathParam","javax.ws.rs.PathParam");

    JaxrsRequestParamAnnotation(String shortName, String qualifiedName) {
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
