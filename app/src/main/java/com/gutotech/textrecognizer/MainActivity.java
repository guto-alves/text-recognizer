package com.gutotech.textrecognizer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Bitmap bitmap;

    private EditText resultEditText;
    private String result;

    private ImageButton detectTextImageButton;
    private ImageButton saveImageButton;
    private ImageButton editImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        resultEditText = findViewById(R.id.resultEditText);
        detectTextImageButton = findViewById(R.id.detectTextImageButton);
        saveImageButton = findViewById(R.id.saveImageButton);
        editImageButton = findViewById(R.id.editImageButton);

        detectTextImageButton.setEnabled(false);
        saveImageButton.setEnabled(false);
    }

    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    public void detect(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_processing);
        dialog.show();

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processText(firebaseVisionText);
                dialog.dismiss();
            }
        });

        detectTextImageButton.setEnabled(false);
        saveImageButton.setEnabled(true);
    }

    private void processText(FirebaseVisionText firebaseVisionText) {
        StringBuilder lines = new StringBuilder();

        for (FirebaseVisionText.TextBlock textBlock : firebaseVisionText.getTextBlocks()) {
            for (FirebaseVisionText.Line line : textBlock.getLines())
                lines.append(line.getText() + "\n");

            lines.append("\n");
        }

        result = lines.toString();
        resultEditText.setText(result);
    }

    public void save(View view) {
    }

    public void edit(View view) {
        resultEditText.setEnabled(!resultEditText.isEnabled());

        if (resultEditText.isEnabled())
            editImageButton.setImageResource(R.drawable.ic_mode_edit_24dp);
        else
            editImageButton.setImageResource(R.drawable.ic_mode_edit_gray_24dp);
    }

    public void copy(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("recognized text", result);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "Resultado copiado", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == 1) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                } else if (requestCode == 2) {
                    Uri uri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                }

                imageView.setImageBitmap(bitmap);

                detectTextImageButton.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
