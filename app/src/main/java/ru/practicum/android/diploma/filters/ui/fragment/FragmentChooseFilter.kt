package ru.practicum.android.diploma.filters.ui.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSelectionBinding
import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Industries
import ru.practicum.android.diploma.filters.domain.models.Region
import ru.practicum.android.diploma.filters.presentation.FiltersViewModel
import ru.practicum.android.diploma.filters.presentation.models.ScreenState
import ru.practicum.android.diploma.filters.ui.adapter.FilterSelectionClickListener
import ru.practicum.android.diploma.filters.ui.adapter.FiltersAdapter
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.SCREEN
import ru.practicum.android.diploma.util.BindingFragment

class FragmentChooseFilter:BindingFragment<FragmentFilterSelectionBinding>() {

    private val viewModel by viewModel<FiltersViewModel>()
    private var adapter:FiltersAdapter? = null
    private var screen:Int? =null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFilterSelectionBinding {
        return FragmentFilterSelectionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getScreenStateLiveData().observe(requireActivity()){chooseScreen(it)}
        screen = arguments?.getInt(SCREEN)
        viewModel.setScreen(screen!!)
        initAdapter()
        binding.recyclerViewFilters.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFilters.adapter = adapter


    }
    private fun initAdapter(){
        adapter = FiltersAdapter(object: FilterSelectionClickListener {
            override fun onClickRegion(model: Region?) {
            }
            override fun onClickIndustries(model: Industries?) {
            }
            override fun onClickCountry(model: Country?) {
            }
        })
    }
    private fun back(){
        binding.arrowback.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun chooseScreen(state:ScreenState){
        when(state){
            ScreenState.showPlaceOfWorkScreen -> showPlaceOfWorkScreen()
            ScreenState.showIndustriesScreen -> showIndustriesScreen()

        }
    }
    private fun showPlaceOfWorkScreen(){
        binding.recyclerViewFilters.visibility = View.GONE
        binding.chooseCountryBottom.visibility = View.VISIBLE
        binding.textViewChoose.visibility = View.VISIBLE
        binding.regionButton.visibility = View.VISIBLE
        binding.region.visibility = View.VISIBLE
        binding.chooseTextview.text = requireActivity().getText(R.string.choose_place_of_work)

    }
    private fun showIndustriesScreen(){

    }

}