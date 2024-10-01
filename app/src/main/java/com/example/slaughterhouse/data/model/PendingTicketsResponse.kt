package com.example.slaughterhouse.data.model

import com.google.gson.annotations.SerializedName

data class PendingTicketsResponse(
    @SerializedName("Id")
    val id: Int,

    @SerializedName("SlNo")
    val slNo: Int,

    @SerializedName("Ticket NO")
    val ticketNO: String,

    @SerializedName("Q Service (En)")
    val qServiceEn: String,

    @SerializedName("Q Service (Ar)")
    val qServiceAr: String,

    @SerializedName("RefNo")
    val refNo: String,

    @SerializedName("TerminalId")
    val terminalId: Int,

    @SerializedName("Registered Time")
    val registeredTime: String,

    @SerializedName("Call Considering Time")
    val callConsideringTime: String,

    @SerializedName("Type")
    val type: String,

    @SerializedName("NationalID")
    val nationalID: String?, // Nullable type

    @SerializedName("Status")
    val status: Int
)
