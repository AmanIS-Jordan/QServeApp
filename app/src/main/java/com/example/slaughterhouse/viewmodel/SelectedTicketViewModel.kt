package com.example.slaughterhouse.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slaughterhouse.data.model.PendingTicketsResponse
import com.example.slaughterhouse.data.model.SelectedTicketReponse
import com.example.slaughterhouse.data.repository.Repository
import com.example.slaughterhouse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectedTicketViewModel @Inject constructor(private val apiRepository: Repository) : ViewModel() {

    val result = MutableLiveData<Resource<PendingTicketsResponse?>?>()


    fun getSelectedTicket(counter: String, branchCode: String){
        viewModelScope.launch {

            result.postValue(Resource.Loading(true))
            val response = apiRepository.getSelectedTicket(counter, branchCode)

            when (response){
                is Resource.Success ->{
                    result.postValue(Resource.Success(response.data))
                }
                is Resource.Error ->
                    result.postValue(Resource.Error(response.message))
                is Resource.Loading->
                    result.postValue(Resource.Loading(true))
            }



        }
    }


}