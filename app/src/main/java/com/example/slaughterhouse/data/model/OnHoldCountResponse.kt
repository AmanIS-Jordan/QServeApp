package com.example.slaughterhouse.data.model

import com.google.gson.annotations.SerializedName

data class OnHoldCountResponse(
@SerializedName("CountHold"            ) var CountHold          : String? = null,
@SerializedName("Allow_Hold"           ) var AllowHold          : String? = null,
@SerializedName("Allow_Reject"         ) var AllowReject        : String? = null,
@SerializedName("Allow_Serve_Normally" ) var AllowServeNormally : String? = null

)
