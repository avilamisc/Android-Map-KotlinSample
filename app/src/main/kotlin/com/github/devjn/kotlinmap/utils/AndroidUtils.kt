package com.github.devjn.kotlinmap.utils

import android.content.Context
import com.github.devjn.kotlinmap.Common
import com.github.devjn.kotlinmap.common.Consts
import com.github.devjn.kotlinmap.common.utils.NativeUtilsResolver
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream


/**
 * Created by @author Jahongir on 18-Apr-17
 * devjn@jn-arts.com
 * AndroidUtils
 */

class AndroidUtils : NativeUtilsResolver {

    override val mapFilePath: String
        get() = Common.applicationContext.filesDir.path + File.separator + Consts.MAP_FILENAME

    override var placesVersion: Int
        get() = Common.placesVersion
        set(value) {
            Common.placesVersion = value
        }

    override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()

    override fun openRawResource(filename: String): InputStream = Common.applicationContext.getResources().openRawResource(
            Common.applicationContext.resources.getIdentifier(filename,
                    "raw", Common.applicationContext.packageName));

    override fun openFileInputStreamFor(filename: String): FileInputStream = Common.applicationContext.openFileInput(filename)

    override fun getFileOutputStreamFor(filename: String): FileOutputStream = Common.applicationContext.openFileOutput(filename, Context.MODE_PRIVATE)

}