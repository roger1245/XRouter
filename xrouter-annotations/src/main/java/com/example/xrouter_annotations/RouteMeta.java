package com.example.xrouter_annotations;


import javax.lang.model.element.Element;

public class RouteMeta {
    private Element element;

    private Class<?> destination;

    private String path;

    private String group;

    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    private RouteType type;

    public RouteMeta() {

    }

    public static RouteMeta build(RouteType type, Class<?> destination, String path) {
        return new RouteMeta(null , destination, path, type);
    }

    public RouteMeta(Route route, Element element, RouteType type) {
        this(element, null, route.path(), type);
    }

    public RouteMeta(Element element, Class<?> destination, String path, RouteType type) {
        this.destination = destination;
        this.element = element;
        this.path = path;
        this.type = type;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
