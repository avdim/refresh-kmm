/*
 * Copyright 2020-2021 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package example.imageviewer

import kotlinx.coroutines.*

internal inline fun getAppScope(): CoroutineScope = MainScope() + Job()
//    CoroutineScope(SupervisorJob() + newSingleThreadContext("mySingleThreadContext"))

val APP_SCOPE by lazy { getAppScope() }

fun launchAppScope(block: suspend () -> Unit) {
    APP_SCOPE.launch {
        block()
    }
}
