package com.example.xroute_core;

import com.example.xrouter_annotations.RouteMeta;

import java.util.Map;

public interface IRouteGroup {
    void loadInto(Map<String, RouteMeta> atlas);
}

//demo如下
//public class EaseRouter_Group_show implements IRouteGroup {
//    @Override
//    public void loadInto(Map<String, RouteMeta> atlas) {
//        atlas.put("/show/info",RouteMeta.build(RouteMeta.Type.ACTIVITY,ShowActivity.class,"/show/info","show"));
//    }
//}