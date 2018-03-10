package com.zhaow.restful.annotations;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePathMapping {
    static List<PathMappingAnnotation> pathMappings = new ArrayList<>();

    public List<PathMappingAnnotation> getPathMappings() {
        return pathMappings;
    }

    /*need override*/
    public void addToPathList(PathMappingAnnotation mapping) {
        pathMappings.add(mapping);
    }


}
