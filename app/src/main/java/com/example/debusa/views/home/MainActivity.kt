package com.example.debusa.views.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.debusa.R
import com.example.debusa.data.remote.response.PredictResponse
import com.example.debusa.data.remote.response.Quiz
import com.example.debusa.data.remote.retrofit.ApiConfig
import com.example.debusa.databinding.ActivityMainBinding
import com.example.debusa.utils.getImageUri
import com.example.debusa.utils.reduceFileImage
import com.example.debusa.utils.uriToFile
import com.example.debusa.views.ViewModelFactory
import com.example.debusa.views.history.HistoryActivity
import com.example.debusa.views.result.ResultActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted : Boolean ->
            if (isGranted){
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            }else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }




    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.gallerybut.setOnClickListener { startGallery() }
        binding.camerabut.setOnClickListener {
            if (!allPermissionGranted()){
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            }else{
                startCamera()
            }
        }


        binding.predictbut.setOnClickListener { analyze() }

        binding.btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

//        viewModel.loading().observe(this){
//            showLoading(it)
//        }
//
//        viewModel.predictResuult.observe(this){response ->
//            if (response.status == "error"){
//                showToast(response.message)
//            }else if(response.status == "success"){
//                moveToResult(
//                    response.data?.result,
//                    response.data?.description,
//                    response.data?.benefit)
//            }
//        }


    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){isSuccess ->
        if (isSuccess) {
            showImage()
        }else{
            currentImageUri = null
        }
    }

    private fun analyze(){
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()

            showLoading(true)
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestImageFile
            )
            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val successResponse = apiService.uploadImage(multipartBody)
                    if (successResponse.status == "success"){
                         moveToResult(
                             successResponse.data?.result,
                             successResponse.data?.description,
                             successResponse.data?.benefit,
                             successResponse.data?.url,
                             successResponse.data?.quiz?.question,
                             successResponse.data?.quiz?.options
                             )
//                        binding.test.text = successResponse.data?.hasil ?: "error"
                    }else{
                        showToast("error")
                    }

                    showLoading(false)
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    if (errorBody != null && errorBody.startsWith("{")) {
                        // Respons berupa objek JSON, lanjutkan deserialisasi
                        val errorResponse = Gson().fromJson(errorBody, PredictResponse::class.java)
                        showToast(errorResponse.message ?: "Unknown error")
                    } else {
                        // Respons bukan objek JSON, tampilkan pesan kesalahan langsung
                        showToast("Opps ada kesalahan,Coba Lagi")
                    }
                    showLoading(false)
                }catch (e: IOException){
                   showLoading(false)
                    showToast("Cpnnection Error")
                }catch (e: Exception) {
                    showLoading(false)
                    showToast("Opppss Ada Kesalahan,Coba Lagi")
                }
            }
        }?: showToast("Opps,Ada Kesalahan,Mohon Masukkan Gambar Kembali")

    }



    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            Glide.with(binding.root)
                .load(it)
                .apply(RequestOptions().transform(RoundedCorners(70)))
                .into(binding.imageprev)
        }
    }


    private fun moveToResult(hasil: String?,desc: String?, manfaat: String?, url: String?, quest:String?, option:List<String?>?) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
        intent.putExtra("HASIL",hasil)
        intent.putExtra("DESC", desc)
        intent.putExtra("MANFAAT", manfaat)
        intent.putExtra("URL", url)
        intent.putExtra("PERTANYAAN", quest)

        intent.putStringArrayListExtra("OPTIONS", ArrayList(option))
        startActivity(intent)
    }


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
    }
}