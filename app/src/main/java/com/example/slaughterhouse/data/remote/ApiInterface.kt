package com.example.slaughterhouse.data.remote


import com.example.slaughterhouse.data.model.BranchesResponse
import com.example.slaughterhouse.data.model.CountersResponse
import com.example.slaughterhouse.data.model.HoldTicketsList
import com.example.slaughterhouse.data.model.LoginSucessResponse
import com.example.slaughterhouse.data.model.OnHoldCountResponse
import com.example.slaughterhouse.data.model.PendingTicketsResponse
import com.example.slaughterhouse.data.model.SelectedTicketReponse
import retrofit2.Response
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


    @GET("api/AndriodGetPending")
    suspend fun getPending(
        @Query("counter") counter: String,
        @Query("BranchCode") branchCode: String,
    ): List<PendingTicketsResponse>


    @GET("api/AndriodGetHoldCount")
    suspend fun getHoldCount(
        @Query("counter") counter: String,
        @Query("BranchCode") branchCode: String,
    ): OnHoldCountResponse


    @GET("api/AndriodGetHoldingTicket")
    suspend fun getHoldTicketsList(
        @Query("counter") counter: String,
        @Query("BranchCode") branchCode: String,
    ): MutableList<HoldTicketsList>


    @GET("api/AndriodRecallTicket")
    suspend fun recallTicket(
        @Query("counter") counter: String,
        @Query("Userid") userid: String,
        @Query("TicketNo") ticketNo: String,
        @Query("TicketId") ticketId: String,
        @Query("RefNo") refNo: String
    ): Response<Unit> // No response body, just a status code

    @GET("api/AndriodServeTicket")
    suspend fun proceedTicket(
        @Query("counter") counter: String,
        @Query("BranchCode") branchCode: String,
        @Query("TicketNo") ticketNo: String,
        @Query("Userid") userid: String,
        @Query("TicketId") ticketId: String
    ): Response<Unit> // No response body, just a status code


    @GET("api/AndroidHoldTicket")
    suspend fun holdTicket(
        @Query("counter") counter: String,
        @Query("BranchCode") branchCode: String,
        @Query("TicketNo") ticketNo: String,
        @Query("Userid") userid: String,
        @Query("TicketId") ticketId: String
    ): Response<Unit> // No response body, just a status code
    @GET("api/AndriodSkipTicket")
    suspend fun skipTicket(
        @Query("counter") counter: String,
        @Query("BranchCode") branchCode: String,
        @Query("TicketNo") ticketNo: String,
        @Query("Userid") userid: String,
        @Query("TicketId") ticketId: String
    ): Response<Unit> // No response body, just a status code

    @GET("api/AndriodGetSelectedTicket")
    suspend fun getSelectedTicket(
        @Query("counter") counter: String,
        @Query("BranchCode") branchCode: String,
    ): PendingTicketsResponse


    @GET("api/AndriodRandomCall")
    suspend fun randomCallTicket(
        @Query("counter") counter: String,
        @Query("Userid") userid: String,
        @Query("TicketNo") ticketNo: String,
        @Query("TicketId") ticketId: String,
        @Query("RefNo") refNo: String,
        @Query("BranchCode") branchCode: String,
    ): Response<Unit> // No response body, just a status code


    companion object {
        const val BASE_URL = "http://192.168.30.50/APIPub2509/"
    }
}