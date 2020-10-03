package com.example.xroute_core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import java.lang.RuntimeException

class XRouter {
    private var mHandler: Handler? = null
    init {
        mHandler = Handler(Looper.getMainLooper())
    }

    companion object {
        private const val ROUTE_ROOT_PACKAGE = "com.example.xrouter.routes"
        public lateinit var mContext: Application
        public fun init(application: Application) {
            mContext = application
            try {
                loadInto()
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

    internal fun navigation(context: Context?, postcard: Postcard, requestCode: Int) {
        _navigation(context, postcard, requestCode)
    }

    private fun _navigation(context: Context?, postcard: Postcard, requestCode: Int) {
        prepareCard(postcard)
        val currentContext = context ?: mContext
        val intent = Intent(currentContext, postcard.destination)
        if (currentContext !is Activity) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        mHandler?.post {
            ActivityCompat.startActivity(currentContext, intent, null)
        }
    }

    private fun prepareCard(card: Postcard) {
        val routeMeta = WareHouse.routes[card.path]
        if (null == routeMeta) {
            val groupMeta = WareHouse.groupIndex[card.group]
            if (null == groupMeta) {
                throw NoRouteFoundException("没有找到对应路由")
            }
            var iGroupInstance: IRouteGroup = groupMeta.getConstructor().newInstance()
            iGroupInstance.loadInto(WareHouse.routes)
            WareHouse.groupIndex.remove(card.group)
            prepareCard(card)
        } else {
            card.destination = routeMeta.destination
        }
    }

}