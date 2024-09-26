package com.example.slaughterhouse.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slaughterhouse.data.model.LoginSucessResponse
import com.example.slaughterhouse.data.repository.Repository
import com.example.slaughterhouse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val apiRepository : Repository) : ViewModel(){

    val result = MutableLiveData<Resource<LoginSucessResponse>>()

    fun login(username: String, password: String){

        viewModelScope.launch {
            result.postValue(Resource.Loading(true))
            val response = apiRepository.login(username, password)

            when (response)
            {
                is Resource.Success ->
                {
                    result.postValue(
                        if (response.data != null) {
                            Resource.Success(response.data) // Non-null case
                        } else {
                            Resource.Error("Data is null") // Handle null case
                        }
                    )
                }
                is Resource.Error->{
                    result.postValue(Resource.Error(response.message))
                }
                is Resource.Loading -> {
                    // You can handle loading state here if needed
                    result.postValue(Resource.Loading(true))
                }
            }


        }
    }


}