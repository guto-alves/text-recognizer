package com.gutotech.textrecognizer

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.gutotech.textrecognizer.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.getBitmap().observe(this, { bitmap: Bitmap ->
            binding.imageView.setImageBitmap(bitmap)
        })
    }

    fun openCamera(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun openGallery(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
    }

    fun save(view: View) {
        if (hasPermission()) {
            viewModel.save()
            Toast.makeText(this, R.string.saved_text, Toast.LENGTH_SHORT).show()
        }
    }

    fun copy(view: View) {
        val clipboardManager: ClipboardManager =
            application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("recognized text", viewModel.recognizedText.value)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, R.string.text_ocupied, Toast.LENGTH_SHORT).show()
    }

    private fun hasPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_EXTERNAL_STORAGE
        )

        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            try {
                var bitmap: Bitmap? = null

                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    bitmap = data!!.extras!!["data"] as Bitmap?
                } else if (requestCode == REQUEST_IMAGE_GET) {
                    val uri: Uri? = data!!.data
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }

                viewModel.setBitmap(bitmap!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.save()
                Toast.makeText(this, R.string.saved_text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private val REQUEST_IMAGE_CAPTURE = 1
        private val REQUEST_IMAGE_GET = 2
        private val REQUEST_WRITE_EXTERNAL_STORAGE = 3
    }
}