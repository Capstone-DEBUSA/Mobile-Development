package com.example.debusa.views.result

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.debusa.R
import com.example.debusa.data.remote.response.PredictResponse
import com.example.debusa.data.remote.retrofit.ApiConfig
import com.example.debusa.databinding.ActivityResultBinding
import com.example.debusa.views.ViewModelFactory
import com.example.debusa.views.history.HistoryActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

class ResultActivity : AppCompatActivity() {
    private val _question = MutableLiveData<String?>()
    val question : LiveData<String?> = _question
    lateinit var binding: ActivityResultBinding
    var jawaban:String? = null
    private val viewModel by viewModels<ResultViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar>(R.id.menuActivity)

        toolbar.setNavigationOnClickListener {
            finish()
        }


        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        val result = intent.getStringExtra("HASIL")
        val desc = intent.getStringExtra("DESC")
        val manfaat = intent.getStringExtra("MANFAAT")
        val url = intent.getStringExtra("URL")
        val quest = intent.getStringExtra("PERTANYAAN")
        val options = intent.getStringArrayListExtra("OPTIONS")

        _question.value = quest


        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            Glide.with(binding.root)
                .load(it)
                .apply(RequestOptions().transform(RoundedCorners(70)))
                .into(binding.resultImage)
        }

        binding.resultText.text = result

        binding.deskripsi.text = desc

        binding.manfaat.text = manfaat

        binding.pertanyaan.text = question.value

        binding.submit.setOnClickListener {
            if (jawaban != null){
                submit(quest.toString(), jawaban!!)
            }else{
                showToast("Opps, Anda Belum Memilih Jawabannya")
            }
        }

        options?.forEachIndexed { index, option ->
            val radioButton = RadioButton(this)
            radioButton.text = option ?: ""
            radioButton.id = index // Atur ID sesuai dengan index opsi
           binding.radioGroup.addView(radioButton)


            // Menambahkan listener untuk menangani perubahan pilihan
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // Aksi yang ingin dilakukan saat RadioButton dipilih
                    // Contoh: Tampilkan pesan atau lakukan operasi lainnya
                    val selectedOption = options[index] ?: ""
                    jawaban = selectedOption
//                    Toast.makeText(this, "Anda memilih: $selectedOption", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.search.setOnClickListener { gotoWiki(url.toString()) }

        binding.btnSave.setOnClickListener {
            viewModel.saveAnalyze(
                result = result.toString(),
                deskripsi = desc.toString(),
                manfaat = manfaat.toString(),
                imageUri = imageUri.toString()
            )
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

    }


    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun submit(quest: String, option: String){
        showLoading(true)
        val quest = quest.toRequestBody("text/plain".toMediaType())
        val option = option.toRequestBody("text/plain".toMediaType())
        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.sendQuestion(quest, option)
                showToast(successResponse.message)

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
    }

    private fun gotoWiki(url:String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }


    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}