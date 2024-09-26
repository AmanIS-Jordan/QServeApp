package com.example.slaughterhouse.data.model

import com.google.gson.annotations.SerializedName

data class CountersResponse (
    @SerializedName("CounterId" ) var CounterId : Int?    = null,
    @SerializedName("CounterEn" ) var CounterEn : String? = null

)