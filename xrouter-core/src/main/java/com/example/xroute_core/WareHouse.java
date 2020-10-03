package com.example.xroute_core;

import com.example.xrouter_annotations.RouteMeta;

import java.util.HashMap;
import java.util.Map;

public class WareHouse {
    static Map<String, Class<? extends IRouteGroup>> groupIndex = new HashMap<>();

    static Map<String, RouteMeta> routes = new HashMap<>();
}
