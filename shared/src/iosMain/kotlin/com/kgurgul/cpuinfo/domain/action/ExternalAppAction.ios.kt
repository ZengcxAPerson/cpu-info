package com.kgurgul.cpuinfo.domain.action

actual class ExternalAppAction actual constructor() {

    actual fun launch(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual fun openSettings(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual fun uninstall(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual fun searchOnWeb(phrase: String): Result<Unit> {
        return Result.success(Unit)
    }
}
