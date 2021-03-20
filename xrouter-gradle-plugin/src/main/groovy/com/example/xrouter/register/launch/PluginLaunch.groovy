package com.example.xrouter.register.launch

import com.android.build.gradle.AppPlugin
import com.example.xrouter.register.core.RegisterTransform
import com.example.xrouter.register.utils.ScanSetting
import org.gradle.api.Plugin
import com.android.build.gradle.AppExtension
import org.gradle.api.Project
import com.example.xrouter.register.utils.Logger

public class PluginLaunch implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def isApp = project.plugins.hasPlugin(AppPlugin)
        if (isApp) {
            Logger.make(project)

            Logger.i('Project enable xrouter-register plugin')

            def android = project.extensions.getByType(AppExtension)
            def transformImpl = new RegisterTransform(project)

            //init router-auto-register settings
            ArrayList<ScanSetting> list = new ArrayList<>(3)
            list.add(new ScanSetting('IRouteRoot'))
            list.add(new ScanSetting('IProviderGroup'))
            ScanSetting.registerList = list
            //register this plugin
            android.registerTransform(transformImpl)
        }
    }
}