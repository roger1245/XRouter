package com.example.xrouter_core.template;

import java.util.Map;

public interface IRouteRoot {
    void loadInto(Map<String, Class<? extends IRouteGroup>> routes);
}
