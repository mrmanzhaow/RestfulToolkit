package com.zhaow.restful.annotations;

import java.util.ArrayList;
import java.util.List;

public interface PathMappingAnnotation {
//    List<PathMappingAnnotation> allPathMappingAnnotations = new ArrayList<>();
    public String getQualifiedName() ;

    public String getShortName();

//    public List<PathMappingAnnotation> getPathMappings();

//    public void addToPathList(PathMapping mapping);

}
