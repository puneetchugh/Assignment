package com.example.assignment.domain

import kotlinx.coroutines.flow.Flow

class CountriesUseCaseImpl private constructor(private val repository: CountriesRepository) :
    CountriesUseCase {
    override operator fun invoke(): Flow<NetworkResult> {
        return repository.getCountries()
    }

    companion object {
        fun getInstance(repository: CountriesRepository): CountriesUseCase {
            return CountriesUseCaseImpl(repository)
        }
    }
}