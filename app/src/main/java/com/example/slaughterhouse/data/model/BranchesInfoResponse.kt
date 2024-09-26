package com.example.slaughterhouse.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize




data class BranchesResponse (
    @SerializedName("BranchCode"   ) var BranchCode   : Int?    = null,
    @SerializedName("BranchNameAr" ) var BranchNameAr : String? = null

)