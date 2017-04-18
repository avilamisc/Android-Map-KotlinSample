/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.devjn.kotlinmap.utils

import android.util.Log

object LogUtils {
    private val LOG_PREFIX = "life_"
    private val LOG_PREFIX_LENGTH = LOG_PREFIX.length
    private val MAX_LOG_TAG_LENGTH = 23

    var LOGGING_ENABLED = true

    fun makeLogTag(str: String): String {
        if (str.length > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1)
        }

        return LOG_PREFIX + str
    }

    /**
     * Don't use this when obfuscating class names!
     */
    fun makeLogTag(cls: Class<*>): String {
        return makeLogTag(cls.simpleName)
    }

    fun LOGD(tag: String, message: String) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, message)
            }
        }
    }

    fun LOGD(tag: String, message: String, cause: Throwable) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, message, cause)
            }
        }
    }

    fun LOGV(tag: String, message: String) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, message)
            }
        }
    }

    fun LOGV(tag: String, message: String, cause: Throwable) {
        if (LOGGING_ENABLED) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, message, cause)
            }
        }
    }

    fun LOGI(tag: String, message: String) {
        if (LOGGING_ENABLED) {
            Log.i(tag, message)
        }
    }

    fun LOGI(tag: String, message: String, cause: Throwable) {
        if (LOGGING_ENABLED) {
            Log.i(tag, message, cause)
        }
    }

    fun LOGW(tag: String, message: String) {
        if (LOGGING_ENABLED) {
            Log.w(tag, message)
        }
    }

    fun LOGW(tag: String, message: String, cause: Throwable) {
        if (LOGGING_ENABLED) {
            Log.w(tag, message, cause)
        }
    }

    fun LOGE(tag: String, message: String) {
        if (LOGGING_ENABLED) {
            Log.e(tag, message)
        }
    }

    fun LOGE(tag: String, message: String, cause: Throwable) {
        if (LOGGING_ENABLED) {
            Log.e(tag, message, cause)
        }
    }
}
