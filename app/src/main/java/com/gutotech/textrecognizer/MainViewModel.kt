package com.gutotech.textrecognizer

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gutotech.textrecognizer.MLKitVisionImage.Listener

class MainViewModel : ViewModel() {
    private val bitmap = MutableLiveData<Bitmap>()

    var recognizedText = MutableLiveData("")

    var editMode = MutableLiveData(false)

    private val mlKitVisionImage = MLKitVisionImage()

    fun getBitmap(): LiveData<Bitmap> {
        return bitmap
    }

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap.value = bitmap
    }

    fun recognizeText() {
        recognizedText.value = "..."

        mlKitVisionImage.recognizeText(bitmap.value!!, object : Listener {
            override fun onRecognizedText(text: String) {
                recognizedText.value = text
            }
        })
    }

    fun save() {
        FileUtils.save(recognizedText.value!!)
    }

    fun changeEditMode() {
        editMode.value = !editMode.value!!
    }
}