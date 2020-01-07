package com.gutotech.textrecognizer;

import android.graphics.Bitmap;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class TextRecognizerRepository {
    private static TextRecognizerRepository instance;

    private TextRecognizerRepository() {
    }

    public static TextRecognizerRepository getInstance() {
        return instance == null ? instance = new TextRecognizerRepository() : instance;
    }

    public void recognizeText(Bitmap bitmap, Listener listener) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        textRecognizer.processImage(image).addOnSuccessListener(firebaseVisionText -> {
            String text = processText(firebaseVisionText);
            listener.onRecognizedText(text);
        });
    }

    private String processText(FirebaseVisionText firebaseVisionText) {
        StringBuilder lines = new StringBuilder();

        for (FirebaseVisionText.TextBlock textBlock : firebaseVisionText.getTextBlocks()) {
            for (FirebaseVisionText.Line line : textBlock.getLines()) {
                lines.append(line.getText());
                lines.append("\n");
            }
            lines.append("\n");
        }

        return lines.toString();
    }

    public void save() {
    }

    public interface Listener {
        void onRecognizedText(String text);
    }
}
