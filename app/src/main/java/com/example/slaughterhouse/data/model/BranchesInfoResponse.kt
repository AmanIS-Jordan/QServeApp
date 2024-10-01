package com.example.slaughterhouse.data.model

import com.google.gson.annotations.SerializedName

data class BranchesResponse (
    @SerializedName("BranchCode"   ) var BranchCode   : Int?    = null,
    @SerializedName("BranchNameAr" ) var BranchNameAr : String? = null

)