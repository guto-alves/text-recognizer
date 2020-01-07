package com.gutotech.textrecognizer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gutotech.textrecognizer.databinding.ActivityMainBinding;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private MainViewModel mViewModel;

    private ImageView imageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        binding.setViewmodel(mViewModel);

        imageView = findViewById(R.id.imageView);

        mViewModel.getBitmap().observe(this, bitmap -> {
            imageView.setImageBitmap(bitmap);
        });

        mViewModel.getShowToast().observe(this, resId -> {
            if (resId != -1) {
                Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = null;
                if (requestCode == REQUEST_IMAGE_CAPTURE)
                    bitmap = (Bitmap) data.getExtras().get("data");
                else if (requestCode == 2) {
                    Uri uri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                }

                mViewModel.setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
