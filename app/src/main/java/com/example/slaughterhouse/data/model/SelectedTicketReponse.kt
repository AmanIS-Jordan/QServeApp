package com.example.slaughterhouse.data.model

import com.google.gson.annotations.SerializedName

data class SelectedTicketReponse (
    @SerializedName("Id") val id: Int,
    @SerializedName("TicketNo") val ticketNo: String,
    @SerializedName("NextQID") val nextQID: Int,
    @SerializedName("Q Service") val qService: String,
    @SerializedName("Q ServiceAr") val qServiceAr: String,
    @SerializedName("Registered Time") val registeredTime: String,
    @SerializedName("EIDANO") val eidano: String
)