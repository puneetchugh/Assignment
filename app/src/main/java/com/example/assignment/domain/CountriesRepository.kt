package com.example.assignment.domain

import kotlinx.coroutines.flow.Flow

interface CountriesRepository {
    fun getCountries(): Flow<NetworkResult>
}