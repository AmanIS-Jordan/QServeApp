package com.example.slaughterhouse.data.repository

import com.example.slaughterhouse.data.model.BranchesResponse
import com.example.slaughterhouse.data.model.CountersResponse
import com.example.slaughterhouse.data.model.LoginSucessResponse
import com.example.slaughterhouse.util.Resource
import okhttp3.MultipartBody
import java.io.File

interface Repository {
    suspend fun login(username: String, password:String) : Resource<LoginSucessResponse>

    suspend fun getBranches(username: String) : Resource<List<BranchesResponse>>

    suspend fun getCounters(username: String,branchCode: String): Resource<List<CountersResponse>>
}
