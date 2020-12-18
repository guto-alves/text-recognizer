package com.gutotech.textrecognizer;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<Bitmap> bitmap = new MutableLiveData<>();

    public MutableLiveData<String> recognizedText = new MutableLiveData<>("");

    public MutableLiveData<Boolean> editMode = new MutableLiveData<>(false);

    private final MutableLiveData<Integer> showToast = new MutableLiveData<>(-1);

    private final MLKitVisionImage mlKitVisionImage = new MLKitVisionImage();

    private final Application mApplication;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
    }

    public LiveData<Bitmap> getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap.setValue(bitmap);
    }

    public LiveData<Integer> getShowToast() {
        return showToast;
    }

    public void recognizeText() {
        recognizedText.setValue("...");

        mlKitVisionImage.recognizeText(bitmap.getValue(), recognizedText::setValue);
    }

    public void save() {
        mlKitVisionImage.save(recognizedText.getValue());
        showToast.setValue(R.string.saved_text);
        showToast.setValue(-1);
    }

    public void changeEditMode() {
        editMode.setValue(!editMode.getValue());
    }

    public void copy() {
        ClipboardManager clipboardManager = (ClipboardManager) mApplication.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("recognized text", recognizedText.getValue());
        clipboardManager.setPrimaryClip(clipData);
        showToast.setValue(R.string.text_ocupied);
        showToast.setValue(-1);
    }
}
