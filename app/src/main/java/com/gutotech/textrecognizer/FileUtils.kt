package com.gutotech.textrecognizer

import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileUtils {
    companion object {
        private const val FOLDER_NAME = "TextRecognizer"

        @JvmStatic
        fun save(fileContent: String) {
            val timeStamp = SimpleDateFormat(" - yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = fileContent.split(" ").toTypedArray()[0] + timeStamp + ".txt"
            val appDirectory = Environment.getExternalStorageDirectory().toString() + "/" + FOLDER_NAME + "/"
            val directory = File(appDirectory)
            directory.mkdirs()
            val file = File(appDirectory, fileName)
            file.parentFile.mkdirs()

            try {
                val fileOutputStream = FileOutputStream(file)
                fileOutputStream.write(fileContent.toByteArray())
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}