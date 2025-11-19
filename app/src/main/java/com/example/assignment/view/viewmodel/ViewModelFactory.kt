package com.example.assignment.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.data.network.NetworkModule
import com.example.assignment.domain.CountriesRepositoryImpl
import com.example.assignment.domain.CountriesUseCaseImpl

class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CountriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CountriesViewModel(
                useCase = CountriesUseCaseImpl.getInstance(
                    repository = CountriesRepositoryImpl.getInstance(
                        apiService = NetworkModule.provideApiService()
                    )
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}