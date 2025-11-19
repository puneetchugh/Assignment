package com.example.assignment.domain

import com.example.assignment.data.api.ApiService
import com.example.assignment.data.model.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CountriesRepositoryImpl private constructor(private val apiService: ApiService) :
    CountriesRepository {

    override fun getCountries(): Flow<NetworkResult> = flow {
        val countries = apiService.getCountries()
        if (countries.isNotEmpty()) {
            emit(NetworkResult.Success(countries = countries))
        } else {
            emit(NetworkResult.Failure(throwable = Throwable(EMPTY_LIST)))
        }
    }.catch {
        emit(NetworkResult.Failure(it))
    }.flowOn(Dispatchers.IO)

    companion object {
        fun getInstance(apiService: ApiService): CountriesRepository {
            return CountriesRepositoryImpl(apiService)
        }

        const val EMPTY_LIST = "empty list"
    }
}

sealed class NetworkResult {
    data class Success(val countries: List<Country>) : NetworkResult()
    data class Failure(val throwable: Throwable) : NetworkResult()
}