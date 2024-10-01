package com.example.slaughterhouse.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slaughterhouse.data.model.HoldTicketsList
import com.example.slaughterhouse.data.remote.ApiInterface
import com.example.slaughterhouse.data.repository.Repository
import com.example.slaughterhouse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoldTicketsListViewModel @Inject constructor(private val apiRepository: Repository) : ViewModel(){

    val result = MutableLiveData<Resource<List<HoldTicketsList>?>>()


    fun getHoldTickets(counter: String, branchCode: String){
        viewModelScope.launch {
            result.postValue(Resource.Loading(true))
            val response = apiRepository.getHoldList(counter, branchCode)

            when(response){
                is Resource.Success-> {
                    result.postValue(Resource.Success(response.data))

                }
                is Resource.Error -> {
                    result.postValue(Resource.Error(response.message))
                }
                is Resource.Error->{
                    result.postValue(Resource.Loading(true))

                }

                else -> {}
            }

        }
    }

}