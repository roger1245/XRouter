package com.example.xroute_core

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.example.xroute_core.XRouter.Companion.sInstance
import com.example.xrouter_annotations.RouteMeta

class Postcard @JvmOverloads constructor(
    path: String?,
    group: String?,
    bundle: Bundle? = null
) : RouteMeta() {
    private val mBundle: Bundle
    fun navigation(): Any? {
        return navigation(null)
    }

    fun navigation(context: Context?): Any? {
        return sInstance.navigation(context, this, -1)
    }

    init {
        setGroup(group)
        setPath(path)
        mBundle = bundle ?: Bundle()
    }
    private var provider: IProvider? = null

    fun setProvider(provider: IProvider): Postcard {
        this.provider = provider
        return this
    }
    fun getProvider(): IProvider? {
        return provider
    }
}