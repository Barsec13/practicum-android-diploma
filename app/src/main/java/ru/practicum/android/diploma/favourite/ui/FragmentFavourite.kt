package ru.practicum.android.diploma.favourite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.adapter.VacancyAdapter
import ru.practicum.android.diploma.databinding.FragmentFavouriteBinding
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.util.BindingFragment

class FragmentFavourite : BindingFragment<FragmentFavouriteBinding>() {

    lateinit var recyclerViewFavourite: RecyclerView
    lateinit var vacancyAdapter: VacancyAdapter
    lateinit var placeholderFavourite: LinearLayout
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavouriteBinding {
        return FragmentFavouriteBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        initAdapter()

        setListeners()
    }

    private fun initViews() {
        recyclerViewFavourite = binding.recyclerView
        placeholderFavourite = binding.placeholderFavourite
    }

    private fun initAdapter() {
        vacancyAdapter = VacancyAdapter(ArrayList<Vacancy>())
        recyclerViewFavourite.adapter = vacancyAdapter
    }

    private fun setListeners() {
        vacancyAdapter.itemClickListener = {position, vacancy ->

        }
    }
}