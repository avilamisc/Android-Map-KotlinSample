package com.github.devjn.kotlinmap.common.utils

import rx.schedulers.Schedulers
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader


/**
 * Created by @author Jahongir on 18-Apr-17
 * devjn@jn-arts.com
 * CommonUtils
 */

object CommonUtils {

    fun write(filename: String, file: String, outputStream: FileOutputStream) {
        try {
            outputStream.write(file.toByteArray(charset("UTF-8")))
            outputStream.close()
        } catch (e: Exception) {
            System.err.println("Failed to write file $filename ,exception: $e")
        }
    }

    fun writeAsync(filename: String, file: String, outputStream: FileOutputStream) {
        Schedulers.io().createWorker().schedule {
            try {
                outputStream.write(file.toByteArray(charset("UTF-8")))
                outputStream.close()
            } catch (e: Exception) {
                System.err.println("Failed to write file $filename ,exception: $e")
            }
        }
    }

    fun readTextFile(inputStream: InputStream): String {
        val isr = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(isr)
        val sb = StringBuilder()

        bufferedReader.readWhile { it != 1 }.forEach {
            sb.append(it)
        }

        bufferedReader.close()
        isr.close()
        inputStream.close()
        return sb.toString()
    }

}

inline fun BufferedReader.readWhile(crossinline predicate: (Int) -> Boolean): Sequence<Char> {
    return generateSequence {
        val c = this.read()
        if (c != -1 && predicate(c)) {
            c.toChar()
        } else {
            null
        }
    }
}