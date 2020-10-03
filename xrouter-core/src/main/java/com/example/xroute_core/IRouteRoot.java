package com.example.xroute_core;

import java.util.Map;

public interface IRouteRoot {
    void loadInto(Map<String, Class<? extends IRouteGroup>> routes);
}
