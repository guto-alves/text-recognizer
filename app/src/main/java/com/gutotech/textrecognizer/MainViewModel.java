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
    private MutableLiveData<Bitmap> bitmap = new MutableLiveData<>();

    public MutableLiveData<String> recognizedText = new MutableLiveData<>("");

    public MutableLiveData<Boolean> editMode = new MutableLiveData<>(false);

    private MutableLiveData<Integer> showToast = new MutableLiveData<>(-1);

    private TextRecognizerRepository mRepository;

    private Application mApplication;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mApplication = application;

        mRepository = TextRecognizerRepository.getInstance();
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

        mRepository.recognizeText(bitmap.getValue(), text -> {
            recognizedText.setValue(text);
        });
    }

    public void save() {
        mRepository.save(recognizedText.getValue());
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
