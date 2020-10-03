package com.example.xroute_core

import android.os.Bundle
import com.example.xroute_core.XRouter.Companion.sInstance
import com.example.xrouter_annotations.RouteMeta

class Postcard @JvmOverloads constructor(
    path: String?,
    group: String?,
    bundle: Bundle? = null
) : RouteMeta() {
    private val mBundle: Bundle
    fun navigation(): Any {
        return sInstance.navigation(null, this, -1)
    }

    init {
        setGroup(group)
        setPath(path)
        mBundle = bundle ?: Bundle()
    }
}