package com.example.xrouter_core.template;

import com.example.xrouter_annotations.RouteMeta;

import java.util.Map;

public interface IProviderGroup {
    /**
     * Load providers map to input
     *
     * @param providers input
     */
    void loadInto(Map<String, RouteMeta> providers);
}
