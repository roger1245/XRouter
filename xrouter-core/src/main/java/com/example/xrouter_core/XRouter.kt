package com.example.xrouter_core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.example.xrouter_core.template.IProvider
import com.example.xrouter_core.template.IProviderGroup
import com.example.xrouter_core.template.IRouteGroup
import com.example.xrouter_core.template.IRouteRoot
import com.example.xrouter_core.utils.Logger
import com.example.xrouter_annotations.RouteType

class XRouter {
    private var mHandler: Handler? = null

    init {
        mHandler = Handler(Looper.getMainLooper())
    }

    companion object {
        private val logger = Logger.logger
        private const val ROUTE_ROOT_PACKAGE = "com.example.xrouter.routes"
        public lateinit var mContext: Application
        public fun init(application: Application) {
            mContext = application
            try {
                LogisticsCenter.loadRouterMap()
                if (LogisticsCenter.registerByPlugin) {
                    logger.i("Load router map by router-auto-register plugin.")

                } else {
                    loadInto()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun loadInto() {
            val rootMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PACKAGE);
            rootMap.forEach { className ->
                if (className.startsWith("$ROUTE_ROOT_PACKAGE.Router_Root_")) {
                    (Class.forName(className).getConstructor()
                        .newInstance() as IRouteRoot).loadInto(WareHouse.groupIndex);
                } else if (className.startsWith("$ROUTE_ROOT_PACKAGE.Router_Providers_")) {
                    (Class.forName(className).getConstructor()
                        .newInstance() as IProviderGroup).loadInto(WareHouse.providersIndex);
                }
            }
        }

        val sInstance: XRouter by lazy {
            XRouter()
        }
    }


    public fun build(path: String): Postcard {
        if (path.isEmpty()) {
            throw RuntimeException("路由地址无效")
        } else {
            return Postcard(path, extractGroup(path));
        }
    }

    private fun extractGroup(path: String): String {
        var startIndex = 0;
        if (path.startsWith("/")) {
            startIndex = 1;
        }
        val defaultGroup = path.substring(startIndex, path.indexOf("/", startIndex))
        if (defaultGroup.isEmpty()) {
            throw RuntimeException("$path: 不能提取Group")
        } else {
            return defaultGroup;
        }
    }

    internal fun navigation(context: Context?, postcard: Postcard, requestCode: Int) =
        _navigation(context, postcard, requestCode)


    private fun _navigation(context: Context?, postcard: Postcard, requestCode: Int): Any? {
        val currentContext = context ?: mContext
        prepareCard(postcard, currentContext)

        when (postcard.type) {
            RouteType.PROVIDER -> {
                return postcard.getProvider()
            }
            RouteType.ACTIVITY -> {
                val intent = Intent(currentContext, postcard.destination)
                if (currentContext !is Activity) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                runInMainThread(Runnable {
                    ActivityCompat.startActivity(
                        currentContext,
                        intent,
                        null
                    )
                })

            }
            else -> {

            }
        }

        return null
    }

    //postcard有了path，但是我们需要拿到Class
    private fun prepareCard(card: Postcard, context: Context) {
        val routeMeta = WareHouse.routes[card.path]
        if (null == routeMeta) {
            val groupMeta = WareHouse.groupIndex[card.group] ?: throw RouteException("没有找到对应路由")
            var iGroupInstance: IRouteGroup = groupMeta.getConstructor().newInstance()
            iGroupInstance.loadInto(WareHouse.routes)
            WareHouse.groupIndex.remove(card.group)
            prepareCard(card, context)
        } else {
            card.destination = routeMeta.destination
            card.type = routeMeta.type

            when (routeMeta.type) {
                RouteType.PROVIDER -> {
                    val providerMeta = routeMeta.destination
                    var instance = WareHouse.providers[providerMeta]
                    if (null == instance) {
                        val provider: IProvider =
                            providerMeta.getConstructor().newInstance() as IProvider
                        provider.init(context)
                        WareHouse.providers[providerMeta] = provider;
                        instance = provider
                    }
                    card.setProvider(instance)
                }
                else -> {
                }
            }
        }
    }

    private fun runInMainThread(runnable: Runnable) {
        if (Looper.getMainLooper().thread !== Thread.currentThread()) {
            mHandler?.post(runnable)
        } else {
            runnable.run()
        }
    }

    public fun <T> navigation(service: Class<out T>, context: Context? = null): T? {
        val postcard: Postcard = buildProvider(service.name) ?: return null
        val currentContext = context ?: mContext
        prepareCard(postcard, currentContext)
        return postcard.getProvider() as (T)
    }

    public fun buildProvider(serviceName: String): Postcard? {
        val meta = WareHouse.providersIndex[serviceName]
        return if (null == meta) {
            null
        } else {
            Postcard(meta.path, extractGroup(meta.path))
        }
    }
}