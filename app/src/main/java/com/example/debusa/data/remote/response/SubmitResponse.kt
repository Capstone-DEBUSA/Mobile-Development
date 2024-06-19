package com.example.debusa.data.remote.response

import com.google.gson.annotations.SerializedName

data class SubmitResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("explanation")
	val explanation: String? = null
)
