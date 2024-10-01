package com.example.slaughterhouse.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slaughterhouse.data.model.CountersResponse
import com.example.slaughterhouse.data.model.OnHoldCountResponse
import com.example.slaughterhouse.data.repository.Repository
import com.example.slaughterhouse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoldCountViewModel  @Inject constructor(private val apiRepository: Repository) : ViewModel() {
    val result = MutableLiveData<Resource<OnHoldCountResponse?>>()

    fun getHoldCount(counter: String, branchCode: String){
        viewModelScope.launch {

            result.postValue(Resource.Loading(true))

            val response = apiRepository.getHoldCount(counter, branchCode)

            when (response){
                is Resource.Success ->
                    result.postValue(Resource.Success(response.data))
                is Resource.Error ->
                    result.postValue(Resource.Error(response.message))
                is Resource.Loading->
                    result.postValue(Resource.Loading(true))
            }
        }

    }



}