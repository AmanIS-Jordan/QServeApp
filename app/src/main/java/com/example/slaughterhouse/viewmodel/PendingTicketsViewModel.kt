package com.example.slaughterhouse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slaughterhouse.data.model.PendingTicketsResponse
import com.example.slaughterhouse.data.repository.Repository
import com.example.slaughterhouse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingTicketsViewModel @Inject constructor(private val apiRepository: Repository) :
    ViewModel() {

    val result = MutableLiveData<Resource<List<PendingTicketsResponse>?>>()


    fun getPendingTickets(counter: String, branchCode: String) {
        viewModelScope.launch {

            result.postValue(Resource.Loading(true))

            val response = apiRepository.getPendingTickets(counter, branchCode)

            when (response) {
                is Resource.Success ->
                    Resource.Success(response.data)?.let { result.postValue(it) }

                is Resource.Error -> {
                    result.postValue(Resource.Error(response.message))
                    Log.v("viewmodel", "error")
                }

                is Resource.Loading -> {
                    result.postValue(Resource.Loading(true))
                    Log.v("viewmodel", "loading")

                }

            }
        }

    }


}