package com.example.assignment.view.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.example.assignment.data.model.Country

class CountriesAdapter : RecyclerView.Adapter<CountriesAdapter.CountryViewHolder>() {

    val countriesList: MutableList<Country> = mutableListOf()

    fun setData(list: List<Country>) {
        if (countriesList.isNotEmpty()) {
            countriesList.clear()
        }
        countriesList.addAll(list.toMutableList())
        notifyDataSetChanged()
    }

    inner class CountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameRegionTextView: TextView = view.findViewById(R.id.tv_name_region)
        private val countryCodeTextView: TextView = view.findViewById(R.id.tv_country_code)
        private val capitalTextView: TextView = view.findViewById(R.id.tv_capital)

        fun bind(position: Int) {
            val countryItem = countriesList[position]
            countryItem.let {
                nameRegionTextView.text = String.format("%s, %s", it.name, it.region)
                countryCodeTextView.text = it.code
                capitalTextView.text = it.capital
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.country_item, parent, false)
        return CountryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return countriesList.size
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(position = position)
    }
}