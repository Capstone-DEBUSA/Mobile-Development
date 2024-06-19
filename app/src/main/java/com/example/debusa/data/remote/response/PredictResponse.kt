package com.example.debusa.data.remote.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Quiz(
	@field:SerializedName("question")
	val question: String? = null,

	@field:SerializedName("options")
	val options: List<String?>? = null
)

data class Data(

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("quiz")
	val quiz: Quiz? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("benefit")
	val benefit: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)
