package com.example.pd_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.*
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface FileUploadService {
    @Multipart
    @POST("/upload/")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ResponseBody>
}

class MainActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val linkToAuth: TextView = findViewById(R.id.contact_button)
        linkToAuth.setOnClickListener {
            val intentC = Intent(this, contacts::class.java)
            startActivity(intentC)
        }

        val GoHistory: TextView = findViewById(R.id.history_button)
        GoHistory.setOnClickListener {
            val intentH = Intent(this, history::class.java)
            startActivity(intentH)
        }

        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            val url = "https://github.com/Xx-Aiser-xX/project_pd"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val loadButton: Button = findViewById(R.id.load_button)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        loadButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        val headerTitleTextView: TextView = findViewById(R.id.header_title)
        val headerTitleText: String = headerTitleTextView.text.toString()
        val spannableString = SpannableString(headerTitleText)
        val startIndex = headerTitleText.indexOf("Пером")

        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.orange)),
            startIndex,
            startIndex + "Пером".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        headerTitleTextView.text = spannableString
    }

    private fun dispatchTakePictureIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                val imagePath: String = saveImageToAppDirectory(imageUri)
                val file = File(imagePath)

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.1.75:8000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service = retrofit.create(FileUploadService::class.java)
                service.uploadImage(body).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(applicationContext, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "Failed to upload image", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToAppDirectory(imageUri: Uri): String {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_", ".jpg", storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
        contentResolver.openInputStream(imageUri).use { inputStream ->
            imageFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
        saveImagePathAndTimestamp(currentPhotoPath, System.currentTimeMillis())
        return currentPhotoPath


    }

    private fun saveImagePathAndTimestamp(path: String, timestampMillis: Long) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val currentIndex = sharedPreferences.getInt("HISTORY_INDEX", 0)
        val editor = sharedPreferences.edit()
        editor.putString("IMAGE_PATH_$currentIndex", path)
        editor.putLong("IMAGE_TIMESTAMP_$currentIndex", timestampMillis)
        val nextIndex = (currentIndex + 1) % 4
        editor.putInt("HISTORY_INDEX", nextIndex)
        editor.apply()
    }
}
