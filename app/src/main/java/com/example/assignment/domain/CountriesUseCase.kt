package com.example.assignment.domain

import kotlinx.coroutines.flow.Flow

interface CountriesUseCase {
    operator fun invoke(): Flow<NetworkResult>
}