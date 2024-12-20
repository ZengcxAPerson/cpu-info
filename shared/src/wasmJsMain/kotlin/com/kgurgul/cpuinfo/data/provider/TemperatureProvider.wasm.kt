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

package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

actual class TemperatureProvider actual constructor() : ITemperatureProvider {

    actual override val sensorsFlow: Flow<TemperatureItem> = emptyFlow()

    actual override fun getBatteryTemperature(): Float? {
        return null
    }

    actual override fun findCpuTemperatureLocation(): String? {
        return ""
    }

    actual override fun getCpuTemperature(path: String): Float? {
        return null
    }
}
