package com.example.slaughterhouse.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slaughterhouse.data.repository.Repository
import com.example.slaughterhouse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HoldTicketViewModel @Inject constructor(private val apiRepository: Repository) : ViewModel() {

    val result = MutableLiveData<Resource<Response<Unit>?>>()

    fun holdTicket(counter: String,userid: String,ticketNo: String,ticketId: String,refNo: String){

        viewModelScope.launch {
            result.postValue(Resource.Loading(true))
            val response = apiRepository.holdTicket(counter, userid, ticketNo, ticketId, refNo)

            when (response){
                is Resource.Success->{
                    result.postValue(Resource.Success(response.data))
                }
                is Resource.Error ->{
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