package com.example.applicationsincidents.ui.incident

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationsincidents.R
import com.example.applicationsincidents.data.api.ApiClient
import com.example.applicationsincidents.util.TokenManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AddIncidentActivity : AppCompatActivity() {

    private lateinit var etType: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSelectImage: Button
    private lateinit var btnSubmit: Button
    private lateinit var ivPreview: ImageView
    private var selectedImageUri: Uri? = null

    private val REQUEST_IMAGE_PICK = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_incident)

        etType = findViewById(R.id.etType)
        etDescription = findViewById(R.id.etDescription)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnSubmit = findViewById(R.id.btnSubmit)
        ivPreview = findViewById(R.id.ivPreview)

        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        btnSubmit.setOnClickListener {
            submitIncident()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            ivPreview.setImageURI(selectedImageUri)
        }
    }

    private fun submitIncident() {
        val type = etType.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (type.isEmpty() || description.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Todos los campos son obligatorios, incluyendo la imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val token = TokenManager.getToken(this)
        if (token == null) {
            Toast.makeText(this, "Token no encontrado. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
            return
        }

        val imageFile = getFileFromUri(selectedImageUri!!)
        if (imageFile == null) {
            Toast.makeText(this, "No se pudo procesar la imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val mimeType = contentResolver.getType(selectedImageUri!!) ?: "image/jpeg"
        val imageRequestBody = imageFile.asRequestBody(mimeType.toMediaType())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)

        val typePart = type.toRequestBody("text/plain".toMediaType())
        val descriptionPart = description.toRequestBody("text/plain".toMediaType())

        val call = ApiClient.apiService.addIncident(
            token = "Bearer $token",
            typeIncident = typePart,
            description = descriptionPart,
            image = imagePart
        )

        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful && response.body()?.get("message") == "Incidente registrado correctamente") {
                    Toast.makeText(this@AddIncidentActivity, "Incidente registrado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error al registrar"
                    Toast.makeText(this@AddIncidentActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(this@AddIncidentActivity, "Error de red: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val mimeType = contentResolver.getType(uri) ?: return null
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("incident_img", ".$extension", cacheDir)
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
