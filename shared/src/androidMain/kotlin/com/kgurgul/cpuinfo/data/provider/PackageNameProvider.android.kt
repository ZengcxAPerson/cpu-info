package com.kgurgul.cpuinfo.data.provider

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class PackageNameProvider actual constructor() : KoinComponent {

    private val context: Context by inject()

    actual suspend fun getPackageName(): String {
        return context.packageName
    }
}
