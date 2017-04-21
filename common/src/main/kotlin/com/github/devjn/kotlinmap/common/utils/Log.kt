package com.github.devjn.kotlinmap.common.utils

import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import org.pmw.tinylog.Logger


/**
 * Created by @author Jahongir on 19-Apr-17
 * devjn@jn-arts.com
 * Log
 */
object Log {

    val LOGGING_ENABLED = true

    init {
        Configurator.defaultConfig()
                .level(if (LOGGING_ENABLED) Level.DEBUG else Level.OFF)
                .activate()
    }

    fun i(tag: String, message :String) =  Logger.info("{} {}", tag, message)

    fun d(tag: String, message :String) =  Logger.debug("{} {}", tag, message)

    fun w(tag: String, message :String) =  Logger.warn("{} {}", tag, message)

    fun e(tag: String, message :String) =  Logger.error("{} {}", tag, message)

    fun wtf(tag: String, message :String) =  Logger.error("WTF {} {}", tag, message)

}