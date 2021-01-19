package com.gutotech.textrecognizer

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition

class MLKitVisionImage {

    interface Listener {
        fun onRecognizedText(text: String)
    }

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
}