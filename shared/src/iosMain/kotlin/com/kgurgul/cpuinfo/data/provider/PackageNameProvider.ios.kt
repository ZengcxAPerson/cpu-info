package com.kgurgul.cpuinfo.data.provider

actual class PackageNameProvider actual constructor() {

    actual suspend fun getPackageName(): String {
        return "com.kgurgul.cpuinfo"
    }
}
