package com.example.slaughterhouse.di

import android.content.Context
import android.util.Log
import com.example.slaughterhouse.data.remote.ApiInterface
import com.example.slaughterhouse.data.repository.Repository
import com.example.slaughterhouse.data.repository.UserRepositoryImpl
import com.example.slaughterhouse.util.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApi(@ApplicationContext context: Context) : ApiInterface {
        val baseUrl = PreferenceManager.getBaseUrl(context) ?: ""

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiInterface::class.java)

    }


    @Provides
    @Singleton
    fun provideRepository(apiInterface: ApiInterface): Repository {
        return UserRepositoryImpl(apiInterface)
    }

}