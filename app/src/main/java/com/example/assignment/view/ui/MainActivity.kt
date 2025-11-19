package com.example.assignment.view.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.example.assignment.data.model.Country
import com.example.assignment.view.viewmodel.CountriesViewModel
import com.example.assignment.view.viewmodel.UiState
import com.example.assignment.view.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModelProvider by lazy { ViewModelFactory() }
    private lateinit var viewModel: CountriesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var error: ImageView
    private lateinit var countriesAdapter: CountriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, viewModelProvider)[CountriesViewModel::class.java]
        countriesAdapter = CountriesAdapter()
        bindViews()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bindDataObservers()
    }

    private fun bindDataObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.countriesUiState.collectLatest {
                    when (it) {
                        is UiState.Data -> showData(it.countries)
                        is UiState.Loading -> showLoading()
                        is UiState.Error -> showError(it.error)
                    }
                }
            }
        }
    }

    private fun bindViews() {
        findViewById<Toolbar>(R.id.app_toolbar).also {
            it.title = getString(R.string.app_name)
        }
        recyclerView = findViewById<RecyclerView>(R.id.id_recycler_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = countriesAdapter
        }
        progressBar = findViewById(R.id.id_loading_progressbar)
        error = findViewById(R.id.id_error)
    }

    private fun showData(listOfCountries: List<Country>) {
        recyclerView.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        error.visibility = View.INVISIBLE
        countriesAdapter.setData(listOfCountries)
    }

    private fun showError(errorMsg: String) {
        error.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        showSnackbar(errorMsg)
    }

    private fun showLoading() {
        error.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun showSnackbar(message: String) {
        val view = findViewById<ConstraintLayout>(R.id.main)
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}

