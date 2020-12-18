package com.gutotech.textrecognizer

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MLKitVisionImage {
    private val FOLDER = "TextRecognizer"

    fun recognizeText(bitmap: Bitmap, listener: Listener) {
        val image = InputImage.fromBitmap(bitmap, 0)

        val recognizer = TextRecognition.getClient()

        recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val text = extractText(visionText)
                    listener.onRecognizedText(text)
                }
                .addOnFailureListener { e ->
                    // TODO: add fail message
                    Log.e("MYAPP", e.message.toString())
                }
    }

    private fun extractText(result: Text): String {
        val lines = StringBuilder()

        for (textBlock in result.textBlocks) {
            for (line in textBlock.lines) {
                lines.append(line.text)
                lines.append("\n")
            }

            lines.append("\n")
        }

        return lines.toString()
    }

    interface Listener {
        fun onRecognizedText(text: String?)
    }

    fun save(fileContent: String) {
        val timeStamp = SimpleDateFormat(" - yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = fileContent.split(" ").toTypedArray()[0] + timeStamp + ".txt"
        val appDirectory = Environment.getExternalStorageDirectory().toString() + "/" + FOLDER + "/"
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