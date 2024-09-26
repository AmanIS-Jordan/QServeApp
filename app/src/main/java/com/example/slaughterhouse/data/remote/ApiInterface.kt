package com.example.slaughterhouse.data.remote


import com.example.slaughterhouse.data.model.BranchesResponse
import com.example.slaughterhouse.data.model.CountersResponse
import com.example.slaughterhouse.data.model.LoginSucessResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("api/Login")
    suspend fun loginApi(
        @Query("txtUserName") username: String,
        @Query("txtPasswod") password: String
    ): LoginSucessResponse


    @GET("api/GetBranches")
    suspend fun getBranches(
        @Query("txtUserName") username: String,
    ): List<BranchesResponse>


    @GET("api/GetCounters")
    suspend fun getCounters(
        @Query("txtUserName") username: String,
        @Query("BranchCode") branchCode: String,
        ): List<CountersResponse>



    companion object {
        const val BASE_URL = "http://192.168.30.50/APIPub2509/"
    }
}