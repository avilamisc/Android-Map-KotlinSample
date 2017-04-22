package com.github.devjn.kotlinmap.utils

import apple.foundation.NSArray
import apple.foundation.NSFileManager
import apple.foundation.NSURL
import apple.foundation.NSUserDefaults
import apple.foundation.enums.NSSearchPathDirectory
import apple.foundation.enums.NSSearchPathDomainMask
import com.github.devjn.kotlinmap.common.Consts
import com.github.devjn.kotlinmap.common.utils.NativeUtilsResolver
import hu.akarnokd.rxjava2.schedulers.BlockingScheduler
import io.reactivex.Scheduler
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream


/**
 * Created by @author Jahongir on 20-Apr-17
 * devjn@jn-arts.com
 * iOSUtils
 */
class iOSUtils : NativeUtilsResolver {

    val preferences: NSUserDefaults

    constructor() {
        preferences = NSUserDefaults.standardUserDefaults()
    }

    override val mapFilePath: String
        get() = ""

    override var placesVersion: Int
        get() = preferences.integerForKey(Consts.NEAR_VERSION).toInt()
        set(value) = preferences.setIntegerForKey(value.toLong(), Consts.NEAR_VERSION)

    override fun mainThread(): Scheduler = BlockingScheduler()

    override fun getFileOutputStreamFor(filename: String): FileOutputStream = FileOutputStream(applicationDocumentsDirectory() + "/" + filename)

    override fun openFileInputStreamFor(filename: String): FileInputStream = FileInputStream(applicationDocumentsDirectory() + "/" + filename)

    override fun openRawResource(filename: String): InputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(filename)

    fun applicationDocumentsDirectory(): String {
        val urls: NSArray<out NSURL>? = NSFileManager.defaultManager().URLsForDirectoryInDomains(NSSearchPathDirectory.DocumentDirectory, NSSearchPathDomainMask.UserDomainMask);
        val url: NSURL = urls!!.lastObject();
        val path = url.fileSystemRepresentation();
        return path;
    }


}