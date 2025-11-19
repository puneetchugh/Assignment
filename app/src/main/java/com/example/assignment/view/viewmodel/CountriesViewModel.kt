package com.example.assignment.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.data.model.Country
import com.example.assignment.domain.CountriesUseCase
import com.example.assignment.domain.NetworkResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CountriesViewModel(private val useCase: CountriesUseCase) : ViewModel() {

    private val trigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val countriesUiState: StateFlow<UiState> = trigger.flatMapLatest { _ ->
        useCase().map {
            when (it) {
                is NetworkResult.Success -> UiState.Data(it.countries)
                is NetworkResult.Failure -> UiState.Error(error = it.throwable.message ?: "")
            }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = UiState.Loading,
        //stopTimeoutMillis is 5 seconds for when there a configuration change like screen-rotation
        //the viewmodel countriesUiState will keep emitting until 5 seconds when the UI is recreated
        //after screen rotation or the UI is sluggish, this will give 5 seconds time. So, this will avoid
        //necessary network call if it's subscribed to in 5 seconds, which is more than enough
        //Taken this 5 seconds time from ANR rule of if there's no response for 5 seconds
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            trigger.emit(Unit)
        }
    }
}

sealed class UiState {
    data class Data(val countries: List<Country>) : UiState()
    data class Error(val error: String) : UiState()
    data object Loading : UiState()
}