package com.example.xrouter_core;

import com.example.xrouter_core.template.IProvider;
import com.example.xrouter_core.template.IRouteGroup;
import com.example.xrouter_annotations.RouteMeta;

import java.util.HashMap;
import java.util.Map;

public class WareHouse {
    static Map<String, Class<? extends IRouteGroup>> groupIndex = new HashMap<>();

    static Map<String, RouteMeta> routes = new HashMap<>();

    // Cache provider
    static Map<Class, IProvider> providers = new HashMap<>();

    static Map<String, RouteMeta> providersIndex = new HashMap<>();
}
