package com.example.assignment

import com.example.assignment.data.model.Country
import com.example.assignment.data.model.Currency
import com.example.assignment.data.model.Language
import com.example.assignment.domain.CountriesUseCase
import com.example.assignment.domain.NetworkResult
import com.example.assignment.view.viewmodel.CountriesViewModel
import com.example.assignment.view.viewmodel.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CountriesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val countriesUseCase = mockk<CountriesUseCase>(relaxed = true)

    private lateinit var countriesViewModel: CountriesViewModel

    @Before
    fun setUp() {
        countriesViewModel = CountriesViewModel(
            useCase = countriesUseCase
        )
    }

    private val countryItem1 = Country(
        capital = "Washington DC",
        code = "US",
        currency = Currency(
            code = "USD",
            name = "dollar",
            symbol = "$"
        ),
        demonym = "",
        flag = "url",
        language = Language(
            code = "",
            iso639Two = "",
            name = "",
            nativeName = ""
        ),
        name = "United States",
        region = "America"
    )
    private val countryItem2 = countryItem1.copy(
        name = "Egypt",
        region = "North Africa",
        currency = Currency(
            code = "EGP",
            name = "Egyptian pound",
            symbol = "£"
        ),
        capital = "Cairo",
        code = "EG"
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Given countriesUseCase emits Flow of NetworkResult Failure, when refresh is called, viewmodel countries returns Flow of UIState Loading and UiState Error`() =
        runTest {
            val throwable = Throwable("Error encountered")
            coEvery { countriesUseCase.invoke() } returns flowOf(NetworkResult.Failure(throwable))
            val listOfUiStates = mutableListOf<UiState>()
            countriesViewModel.refresh()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                countriesViewModel.countriesUiState.collectLatest {
                    listOfUiStates.add(it)
                }
            }
            Assert.assertEquals(2, listOfUiStates.size)
            Assert.assertTrue(listOfUiStates[0] is UiState.Loading)
            Assert.assertTrue(listOfUiStates[1] is UiState.Error)
            Assert.assertEquals(throwable.message, (listOfUiStates[1] as UiState.Error).error)

        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Given countriesUseCase emits Flow of NetworkResult Success, when refresh is called, viewmodel countries returns Flow of UIState Loading and UiState Success`() =
        runTest {
            val listOfCountries = listOf(countryItem1, countryItem2)
            coEvery { countriesUseCase.invoke() } returns flowOf(
                NetworkResult.Success(
                    listOfCountries
                )
            )
            val listOfUiStates = mutableListOf<UiState>()
            countriesViewModel.refresh()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                countriesViewModel.countriesUiState.collectLatest {
                    listOfUiStates.add(it)
                }
            }
            Assert.assertEquals(2, listOfUiStates.size)
            Assert.assertTrue(listOfUiStates[0] is UiState.Loading)
            Assert.assertTrue(listOfUiStates[1] is UiState.Data)
            Assert.assertEquals(
                listOfCountries,
                (listOfUiStates[1] as UiState.Data).countries
            )
            Assert.assertEquals(countryItem1, (listOfUiStates[1] as UiState.Data).countries[0])
            Assert.assertEquals(countryItem2, (listOfUiStates[1] as UiState.Data).countries[1])
            Assert.assertEquals(
                countryItem2.code,
                (listOfUiStates[1] as UiState.Data).countries[1].code
            )

            Assert.assertEquals(
                countryItem1.currency,
                (listOfUiStates[1] as UiState.Data).countries[0].currency
            )

        }
}