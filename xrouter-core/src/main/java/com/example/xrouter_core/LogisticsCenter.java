package com.example.xrouter_core;

import android.text.TextUtils;

import com.example.xrouter_core.template.IProviderGroup;
import com.example.xrouter_core.template.IRouteRoot;
import com.example.xrouter_core.utils.Logger;

public class LogisticsCenter {
    public static Logger logger = Logger.logger;
    public static boolean registerByPlugin;

    public static void loadRouterMap() {
        registerByPlugin = false;
        //下面插入ASM代码，形如：
        //register(xxx);
    }

    private static void register(String className) {
        if (!TextUtils.isEmpty(className)) {
            try {
                Class<?> clazz = Class.forName(className);
                Object object = clazz.getConstructor().newInstance();
                if (object instanceof IRouteRoot) {
                    registerRouteRoot((IRouteRoot) object);
                } else if (object instanceof IProviderGroup) {
                    registerProvider((IProviderGroup) object);
                } else {
                    logger.i("register failed, class name: " + className
                            + " should implements one of IRouteRoot/IProviderGroup/IInterceptorGroup.");
                }
            } catch (Exception e) {
                logger.e("register class error:" + className);

            }
        }
    }

    private static void registerProvider(IProviderGroup providerGroup) {
        markRegisteredByPlugin();
        if (providerGroup != null) {
            providerGroup.loadInto(WareHouse.providersIndex);
        }
    }

    private static void registerRouteRoot(IRouteRoot routeRoot) {
        markRegisteredByPlugin();
        if (routeRoot != null) {
            routeRoot.loadInto(WareHouse.groupIndex);
        }
    }

    private static void markRegisteredByPlugin() {
        if (!registerByPlugin) {
            registerByPlugin = true;
        }
    }


}
