package com.example.slaughterhouse.data.repository

import com.example.slaughterhouse.data.model.BranchesResponse
import com.example.slaughterhouse.data.model.CountersResponse
import com.example.slaughterhouse.data.model.HoldTicketsList
import com.example.slaughterhouse.data.model.LoginSucessResponse
import com.example.slaughterhouse.data.model.OnHoldCountResponse
import com.example.slaughterhouse.data.model.PendingTicketsResponse
import com.example.slaughterhouse.data.model.SelectedTicketReponse
import com.example.slaughterhouse.util.Resource
import retrofit2.Response
import retrofit2.http.Query

interface Repository {
    suspend fun login(username: String, password: String): Resource<LoginSucessResponse>

    suspend fun getBranches(username: String): Resource<List<BranchesResponse>>

    suspend fun getCounters(username: String, branchCode: String): Resource<List<CountersResponse>>

    suspend fun getHoldCount(counter: String, branchCode: String): Resource<OnHoldCountResponse>

    suspend fun getPendingTickets(
        counter: String,
        branchCode: String
    ): Resource<List<PendingTicketsResponse>>

    suspend fun getHoldList(counter: String, branchCode: String): Resource<MutableList<HoldTicketsList>>

    suspend fun recallTicket(
        counter: String,
        userid: String,
        ticketNo: String,
        ticketId: String,
        refNo: String
    ):Resource<Response<Unit>>


    suspend fun proceedTicket(
        counter: String,
        branchCode: String,
        ticketNo: String,
        userid: String,
        ticketId: String
    ) :Resource<Response<Unit>>

    suspend fun skipTicket(
        counter: String,
        branchCode: String,
        ticketNo: String,
        userid: String,
        ticketId: String
    ) :Resource<Response<Unit>>

    suspend fun holdTicket(
        counter: String,
        branchCode: String,
        ticketNo: String,
        userid: String,
        ticketId: String
    ) :Resource<Response<Unit>>

    suspend fun randomCall(
        counter: String,
        userid: String,
        ticketNo: String,
        ticketId: String,
        refNo: String,
        branchCode: String,
    ) :Resource<Response<Unit>>

    suspend fun getSelectedTicket(counter: String, branchCode: String) : Resource<PendingTicketsResponse>

}
