package com.github.devjn.kotlinmap.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.BindingAdapter
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.devjn.kotlinmap.Common
import com.github.devjn.kotlinmap.R
import com.github.devjn.kotlinmap.common.utils.CommonUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.io.*

/**
 * Created by @author Jahongir on ${date}
 *
 *
 * $Helper
 */


@BindingAdapter("bind:imageUrl")
fun loadImage(imageView: ImageView, v: String) {
    val suffix = "_img_01.jpg"
    Glide.with(imageView.context).load(Helper.DIRECTORY_PATH + v + suffix).into(imageView)
}


@BindingAdapter("bind:imageUrlSample")
fun loadSampleImage(imageView: ImageView, v: String?) {
    Glide.with(imageView.context).load(R.drawable.img_kotlin_feature).into(imageView)
}

object Helper {

    val TAG = Helper::class.java.kotlin.simpleName;

    val DIRECTORY_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    //Environment.getExternalStorageDirectory().toString() + File.separator + "Sample" + File.separator

    fun saveFile(context: Context, fileName: String, file: String): String {
        Log.i(TAG, Environment.getExternalStorageDirectory().toString() + " " + Environment.getRootDirectory())
        val directory = File(DIRECTORY_PATH)
        if (!directory.exists()) {
            // creates misssing parts of directory
            directory.mkdirs()
        }
        val mypath = File(directory, fileName)
        try {
            val fos = FileOutputStream(mypath)
            fos.write(file.toByteArray(charset("UTF-8")))
            fos.close()
            Log.i(TAG + " save", "path= " + mypath.absolutePath)
        } catch (e: FileNotFoundException) {
            Log.w(TAG + " save", "file not found")
        } catch (e: IOException) {
            Log.w(TAG, "file not saved " + e)
        }

        return directory.absolutePath
    }

    fun write(filename: String, file: String) {
        CommonUtils.write(filename, file, Common.applicationContext.openFileOutput(filename, Context.MODE_PRIVATE))
    }

    fun writeAsync(filename: String, file: String) {
        CommonUtils.writeAsync(filename, file, Common.applicationContext.openFileOutput(filename, Context.MODE_PRIVATE))
    }

    fun copyFileToDir(fileName: String, file: File) {
        val directory = File(DIRECTORY_PATH)
        if (!directory.exists()) {
            // creates misssing parts of directory
            directory.mkdirs()
        }
        val mypath = File(directory, fileName)
        try {
            fileCopy(file, mypath)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to copyFile: " + e)
        }
    }

    fun copyFileToDir(fileName: String, ins: InputStream) {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!directory.exists()) {
            // creates misssing parts of directory
            directory.mkdirs()
        }
        val mypath = File(directory, fileName)
        try {
            fileCopy(ins, mypath.outputStream())
        } catch (e: IOException) {
            Log.e(TAG, "Failed to copyFile: " + e)
        }
    }

    // Fastest way to Copy file in Java
    @Throws(IOException::class)
    fun fileCopy(inFile: File, out: File) {
        val inChannel = FileInputStream(inFile).channel
        val outChannel = FileOutputStream(out).channel
        try {
            val maxCount = 64 * 1024 * 1024 - 32 * 1024
            val size = inChannel!!.size()
            var position: Long = 0
            while (position < size) {
                position += inChannel.transferTo(position, maxCount.toLong(), outChannel)
            }
        } finally {
            inChannel?.close()
            outChannel?.close()
        }
    }

    fun fileCopy(ins: InputStream, out: OutputStream) {
        ins.copyTo(out);
    }


    /**
     * Convert a JSON string to pretty print version

     * @param jsonString
     * *
     * @return
     */
    fun toPrettyFormat(jsonString: String, gson: Gson?): String {
        var gson = gson
        val parser = JsonParser()
        val json = parser.parse(jsonString).asJsonObject

        if (gson == null)
            gson = GsonBuilder().setPrettyPrinting().create()
        val prettyJson = gson!!.toJson(json)
        return prettyJson
    }

    /**

     * @param activity
     * *
     * @param mode navigation mode, b=bicycle
     */
    fun startGoogleMapsNavigation(activity: Activity, lat: Float, lng: Float, mode: String?) {
        var uri = "google.navigation:q=$lat,$lng"
        if (mode != null)
            uri += "&mode=" + mode
        val gmmIntentUri = Uri.parse(uri)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.`package` = "com.google.android.apps.maps"
        activity.startActivity(mapIntent)
    }

}