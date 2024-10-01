package com.example.slaughterhouse.data.model

import com.google.gson.annotations.SerializedName

data class HoldTicketsList(
    @SerializedName("Id") val id: String,
    @SerializedName("Ticket NO") val ticketNo: String,
    @SerializedName("Ticket Reference No") val ticketReferenceNo: String,
    @SerializedName("Q ID") val qId: Int,
    @SerializedName("Q Service (En)") val qServiceEn: String,
    @SerializedName("Q Service (Ar)") val qServiceAr: String,
    @SerializedName("Registered Time") val registeredTime: String,
    @SerializedName("National ID") val nationalId: String,
    @SerializedName("MRN") val mrn: String,
    @SerializedName("Cust Name (En)") val custNameEn: String,
    @SerializedName("Cust Name (Ar)") val custNameAr: String,
    @SerializedName("Has Insurance") val hasInsurance: String,
    @SerializedName("Note") val note: String
)
