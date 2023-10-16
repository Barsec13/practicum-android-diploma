package ru.practicum.android.diploma.filters.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSelectionBinding
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Industries
import ru.practicum.android.diploma.filters.domain.models.Region
import ru.practicum.android.diploma.filters.presentation.FiltersViewModel
import ru.practicum.android.diploma.filters.presentation.models.ScreenState
import ru.practicum.android.diploma.filters.presentation.models.ShowViewState
import ru.practicum.android.diploma.filters.ui.adapter.FilterSelectionClickListener
import ru.practicum.android.diploma.filters.ui.adapter.FiltersAdapter
import ru.practicum.android.diploma.filters.ui.fragment.SettingFiltersFragment.Companion.SCREEN
import ru.practicum.android.diploma.util.BindingFragment

class ChooseFilterFragment : BindingFragment<FragmentFilterSelectionBinding>() {

    private val viewModel by viewModel<FiltersViewModel>()
    private var adapter: FiltersAdapter? = null
    private var screen: String? = null
    private var region: Region? = null
    private val areaList = mutableListOf<Region>()
    private val industryList = mutableListOf<Industries>()
    private var industry: Industries? = null
    private var isRegionScreen: Boolean = false
    private var editText: String? = null
    private var _isChecked = false

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFilterSelectionBinding {
        return FragmentFilterSelectionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getScreenStateLiveData().observe(requireActivity()) { chooseScreen(it) }
        viewModel.getShowViewStateLiveData().observe(requireActivity()) { showView(it) }
        screen = arguments?.getString(SCREEN)
        viewModel.setScreen(screen!!)
        initAdapter()
        back()
        binding.recyclerViewFilters.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFilters.adapter = adapter
        applyButton()
        initListeners()

    }

    private fun initAdapter() {
        adapter = FiltersAdapter(object : FilterSelectionClickListener {
            override fun onClickRegion(model: Region?, isChecked: Boolean) {
                when (isChecked) {
                    true -> {
                        _isChecked = isChecked
                        model!!.isChecked = true
                        areaList.map { if (it.equals(region)) it.isChecked = false }
                        adapter?.notifyDataSetChanged()
                        region = model
                    }

                    false -> {
                        _isChecked = isChecked
                        model!!.isChecked = false
                        region = null
                    }
                }
                binding.buttonApply.visibility = View.GONE
                region?.let { binding.buttonApply.visibility = View.VISIBLE }
                Log.d("Area", "$areaList")
            }

            override fun onClickIndustries(model: Industries?, isChecked: Boolean) {
                when (isChecked) {
                    true -> {
                        _isChecked=isChecked
                        model!!.isChecked = true
                        industryList.map { if (it.equals(industry)) it.isChecked = false }
                        adapter?.notifyDataSetChanged()
                        industry = model
                    }

                    false -> {
                        _isChecked = isChecked
                        model!!.isChecked = false
                        industry = null
                    }
                }
                binding.buttonApply.visibility = View.GONE
                industry?.let { binding.buttonApply.visibility = View.VISIBLE }

            }

            override fun onClickCountry(model: Areas?) {
                viewModel.addCountry(model!!)
                findNavController().navigateUp()

            }

        })
    }

    private fun initListeners() {
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) hideKeyBoard()
            viewModel.setOnFocus(binding.searchEditText.text.toString(), hasFocus)

        }
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                editText = s.toString()
                viewModel.setOnFocus(editText, binding.searchEditText.hasFocus())
                search()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.editTextCloseImage.setOnClickListener {
            binding.searchEditText.text.clear()
            binding.searchEditText.clearFocus()
            search()
            hideKeyBoard()
        }
    }

    private fun applyButton() {
        binding.buttonApply.setOnClickListener {
            region?.let {
                viewModel.addArea(it)
            }
            industry?.let {
                viewModel.addIndustries(it)
            }
            findNavController().navigateUp()
        }
    }

    private fun back() {
        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    fun search(){
            when (isRegionScreen) {
                true -> viewModel.searchRegion(binding.searchEditText.text.toString(), _isChecked)
                else -> viewModel.searchIndustry(binding.searchEditText.text.toString(), _isChecked)
            }
    }


    private fun chooseScreen(state: ScreenState) {
        when (state) {
            is ScreenState.ShowIndustriesScreen -> showIndustriesScreen()
            is ScreenState.ShowAreasScreen -> showAreasScreen()
            is ScreenState.ShowCountriesScreen -> {
                showCountriesScreen()
            }
            is ScreenState.ShowIndustryList -> showIndustryList(state.industryList)
            is ScreenState.ShowAreasList -> showAreasList(state.areasList)
            is ScreenState.ShowCountriesList -> showCountriesList(state.countriesList)
        }
    }

    private fun showIndustryList(industry: List<Industries>) {
        industryList.addAll(industry)
        adapter?.setIndustrie(industry)
        if (industry.isEmpty()) {
            binding.placeholderImage.visibility = View.VISIBLE
        } else {
            binding.placeholderImage.visibility = View.GONE
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun showAreasList(region: List<Region>) {
        areaList.addAll(region)
        adapter?.setRegion(region)
        if (region.isEmpty()) {
            binding.placeholderImage.visibility = View.VISIBLE
        } else {
            binding.placeholderImage.visibility = View.GONE
        }
        binding.progressBar.visibility = View.GONE

    }

    private fun showCountriesList(country: List<Areas>) {
        adapter?.setCountry(country)
        if (country.isEmpty()) {
            binding.placeholderImage.visibility = View.VISIBLE
        } else {
            binding.placeholderImage.visibility = View.GONE
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun showCountriesScreen() {
        binding.searchEditText.visibility = View.GONE
        binding.chooseTextview.text = requireActivity().getText(R.string.choose_of_country)
    }

    private fun showIndustriesScreen() {
        isRegionScreen = false
        binding.recyclerViewFilters.visibility = View.VISIBLE
        binding.chooseTextview.text = requireActivity().getText(R.string.choose_of_industry)
        binding.searchEditText.setHint(requireActivity().getText(R.string.choose_of_industry))
        binding.searchEditText.visibility = View.VISIBLE
        if (editText.isNullOrEmpty()) binding.editTextSearchImage.visibility = View.VISIBLE

    }

    private fun showAreasScreen() {
        isRegionScreen = true
        binding.recyclerViewFilters.visibility = View.VISIBLE
        binding.chooseTextview.text = requireActivity().getText(R.string.choose_of_region)
        binding.searchEditText.setHint(requireActivity().getText(R.string.choose_of_region))
        binding.searchEditText.visibility = View.VISIBLE
        if (editText.isNullOrEmpty()) binding.editTextSearchImage.visibility = View.VISIBLE

    }

    private fun hideKeyBoard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
    }

    fun showView(state: ShowViewState) {
        when (state) {
            is ShowViewState.showClearIcon -> showClearIcon()
            is ShowViewState.hideClearIcon -> hideClearIcon()
            else -> {}
        }
    }

    fun showClearIcon() {
        binding.editTextSearchImage.visibility = View.GONE
        binding.editTextCloseImage.visibility = View.VISIBLE
    }

    fun hideClearIcon() {
        binding.editTextSearchImage.visibility = View.VISIBLE
        binding.editTextCloseImage.visibility = View.GONE

    }

}