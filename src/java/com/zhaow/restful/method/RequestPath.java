package com.zhaow.restful.method;

public class RequestPath {
    String path;
    String method;

    public RequestPath(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void concat(RequestPath classRequestPath) {
        String classUri = classRequestPath.getPath();
        String methodUri = this.path;
        //TODO
        if (!classUri.startsWith("/")) classUri = "/".concat(classUri);
        if (!classUri.endsWith("/")) classUri = classUri.concat("/");
        if (this.path.startsWith("/")) methodUri = this.path.substring(1, this.path.length());

        this.path = classUri.concat(methodUri) ;
//        method
    }
}
