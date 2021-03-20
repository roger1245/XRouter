package com.example.xrouter_core.template;

import com.example.xrouter_annotations.RouteMeta;

import java.util.Map;

public interface IRouteGroup {
    void loadInto(Map<String, RouteMeta> atlas);
}

//demo如下
//public class Router_Group_module1 implements IRouteGroup {
//    @Override
//    public void loadInto(Map<String, RouteMeta> atlas) {
//        atlas.put("module1/module1activity", RouteMeta.build(RouteType.ACTIVITY, Module1Activity.class, "module1/module1activity"));
//    }
//}