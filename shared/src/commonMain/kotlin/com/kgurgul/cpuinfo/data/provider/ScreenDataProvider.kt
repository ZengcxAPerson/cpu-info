package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
expect class ScreenDataProvider() {

    suspend fun getData(): List<Pair<String, String>>
}