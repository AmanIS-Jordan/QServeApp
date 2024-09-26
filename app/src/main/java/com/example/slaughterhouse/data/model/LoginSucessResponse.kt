package com.example.slaughterhouse.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginSucessResponse(
    @SerializedName("User_Id") val userId: String?,
    @SerializedName("User_Password") val userPassword: String?,
    @SerializedName("User_Emp_Name") val userEmpName: String?,
    @SerializedName("User_Status") val userStatus: Int?,
    @SerializedName("BranchCode") val branchCode: Int?,
    @SerializedName("SectionCode") val sectionCode: Int?,
    @SerializedName("App_User") val appUser: String?,
    @SerializedName("BranchNameEn") val branchNameEn: String?,
    @SerializedName("SectionNameEn") val sectionNameEn: String?,
    @SerializedName("AllowQueueFiltering") val allowQueueFiltering: Boolean?,
    @SerializedName("AllowQueueResourceFiltering") val allowQueueResourceFiltering: Boolean?

): Parcelable

@Parcelize
data class LoginErrorResponse(
    @SerializedName("result") val result: String?,
    @SerializedName("error_code") val errorCode: Int?,
    @SerializedName("msg_en") val messageEn: String?,
    @SerializedName("msg_ar") val messageAr: String?,
    @SerializedName("User_Id") val userId: String?
): Parcelable
