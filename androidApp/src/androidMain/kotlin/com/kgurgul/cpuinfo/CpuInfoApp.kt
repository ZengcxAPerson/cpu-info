/*
 * Copyright 2017 KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kgurgul.cpuinfo

import android.app.Application
import com.kgurgul.cpuinfo.appinitializers.AppInitializerComponent
import com.kgurgul.cpuinfo.appinitializers.AppInitializersModule
import com.kgurgul.cpuinfo.data.DataModule
import com.kgurgul.cpuinfo.di.androidModule
import com.kgurgul.cpuinfo.domain.DomainModule
import com.kgurgul.cpuinfo.features.FeaturesModule
import com.kgurgul.cpuinfo.utils.UtilsModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

@HiltAndroidApp
class CpuInfoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CpuInfoApp)
            modules(
                androidModule,
                AppInitializersModule().module,
                DataModule().module,
                DomainModule().module,
                FeaturesModule().module,
                UtilsModule().module,
            )
        }
        AppInitializerComponent().init(this)
    }
}