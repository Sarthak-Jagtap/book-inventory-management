package com.bookinventory.ui.model;
public class Endpoint {
    private String method;
    private String path;
    private String title;
    private String description;

    public Endpoint(String method, String path, String title, String description) {
        this.method = method;
        this.path = path;
        this.title = title;
        this.description = description;
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    
    public String getFormattedPath() {
        if (this.path == null) return "";
        return this.path.replace("{", "<span class=\"param\">{").replace("}", "}</span>");
    }
}
