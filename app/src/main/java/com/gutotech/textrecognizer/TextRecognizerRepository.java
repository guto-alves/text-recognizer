package com.gutotech.textrecognizer;

import android.graphics.Bitmap;
import android.os.Environment;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextRecognizerRepository {
    private static final String FOLDER = "TextRecognizer";

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

    public interface Listener {
        void onRecognizedText(String text);
    }

    public void save(String fileContent) {
        String timeStamp = new SimpleDateFormat(" - yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = fileContent.split(" ")[0] + timeStamp + ".txt";

        String appDirectory = Environment.getExternalStorageDirectory() + "/" + FOLDER + "/";

        File directory = new File(appDirectory);
        directory.mkdirs();

        File file = new File(appDirectory, fileName);
        file.getParentFile().mkdirs();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(fileContent.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
