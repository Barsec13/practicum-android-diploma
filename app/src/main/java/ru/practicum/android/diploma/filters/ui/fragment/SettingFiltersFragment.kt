package ru.practicum.android.diploma.filters.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSettingFiltersBinding
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.filters.presentation.FiltersViewModel
import ru.practicum.android.diploma.filters.presentation.models.FiltersDataState
import ru.practicum.android.diploma.filters.presentation.models.ShowViewState
import ru.practicum.android.diploma.search.ui.SearchFragment
import ru.practicum.android.diploma.util.BindingFragment
import ru.practicum.android.diploma.util.InputTextChangeHandler
import ru.practicum.android.diploma.util.app.App

class SettingFiltersFragment : BindingFragment<FragmentSettingFiltersBinding>() {

    private val viewModel by viewModel<FiltersViewModel>()
    private var bundle: Bundle? = null
    private lateinit var getFilters: Filters
    private val inputTextChangeHandler = InputTextChangeHandler()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingFiltersBinding {
        return FragmentSettingFiltersBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getShowViewStateLiveData().observe(requireActivity()) { showView(it) }
        switchToPlaceOfWorkScreen()
        switchToIndustriesScreen()
        back()
        viewModel.showFiltersData()
        doOnTextChanged()
        initListeners()
        viewModel.getFiltersStateLiveData().observe(requireActivity()) {
            render(it)
        }
    }

    override fun onDetach() {
        super.onDetach()
        App.DATA_HAS_CHANGED = "no"
    }
    private fun initListeners(){
        binding.salaryEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyBoard()
                binding.salaryEditText.clearFocus()
                binding.clearIcon.visibility = View.GONE
                true
            } else {
                false
            }
        }

        binding.salaryEditText.setOnFocusChangeListener { _, hasFocus ->
            setSalaryEditTextColor(binding.salaryEditText.text.toString(), hasFocus)
            viewModel.setOnFocus(binding.salaryEditText.text.toString(), hasFocus)
            binding.clearIcon.visibility = View.GONE
        }
        binding.placeOfWorkClear.setOnClickListener {
            clearPlaceWork()
        }
        binding.industryClear.setOnClickListener {
            clearIndustries()
        }
        binding.clearAll.setOnClickListener {
            clearPlaceWork()
            clearIndustries()
            viewModel.writeFilters()
            clearEditTextSallary()
            viewModel.addOnlyWithSalary(false)
            binding.filterCheckbox.isChecked = false
            binding.clearAll.visibility = View.GONE
            binding.buttonApply.visibility = View.VISIBLE
        }
        binding.buttonApply.setOnClickListener {
            viewModel.writeFilters()
            findNavController().navigate(
                R.id.action_settingFilters_to_searchFragment,
                SearchFragment.createArgs(Gson().toJson(getFilters))
            )
        }
        binding.clearIcon.setOnClickListener {
            clearEditTextSallary()
            binding.salaryEditText.clearFocus()
            hideKeyBoard()
        }
        binding.filterCheckbox.setOnClickListener {
            viewModel.addOnlyWithSalary(binding.filterCheckbox.isChecked)
            binding.buttonApply.visibility = View.VISIBLE
        }
        binding.editTextBackground.setOnClickListener {
            binding.salaryEditText.requestFocus()
            showKeyBoard()
            binding.salaryEditText.setSelection(binding.salaryEditText.text.length)
        }

    }

    private fun hideKeyBoard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.salaryEditText.windowToken, 0)
        binding.salaryEditText.clearFocus()
    }

    private fun showKeyBoard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun switchToPlaceOfWorkScreen() {
        binding.placeOfWorkButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingFilters_to_fragmentPlaceOfWork)
        }
    }

    fun switchToIndustriesScreen() {
        binding.industryButton.setOnClickListener {
            bundle = bundleOf(SCREEN to INDUSTRIES)
            findNavController().navigate(R.id.action_settingFilters_to_fragmentChooseFilter, bundle)
        }
    }

    fun back() {
        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun render(state: FiltersDataState) {
        when (state) {
            is FiltersDataState.filtersData -> showFiltersData(state.filters)

        }
    }

    private fun showFiltersData(filters: Filters) {
        viewModel.hasDataChanged()
        getFilters = filters
        var placeOfWork = ""
        var industries = ""
        filters.countryName?.let {
            placeOfWork = it
            binding.placeOfWorkEditText.setText(placeOfWork)
            binding.placeOfWorkButton.visibility = View.INVISIBLE
            binding.placeOfWorkClear.visibility = View.VISIBLE
        }

        filters.areasNames?.let {
            placeOfWork += ", $it"
            binding.placeOfWorkEditText.setText(placeOfWork)
        }

        filters.industriesName?.let {
            industries += "$it "
            binding.industryEditText.setText(industries)
            binding.industryButton.visibility = View.INVISIBLE
            binding.industryClear.visibility = View.VISIBLE
        }

        if (filters.salary != 0) {
            binding.salaryEditText.setText(filters.salary.toString())
        }

        if (filters.onlyWithSalary != false) {
            binding.filterCheckbox.isChecked = true
        }
    }

    private fun clearPlaceWork() {
        binding.placeOfWorkEditText.text?.clear()
        binding.placeOfWorkClear.visibility = View.GONE
        binding.placeOfWorkButton.visibility = View.VISIBLE
        viewModel.clearCountry()
        viewModel.clearRegion()
    }

    private fun clearIndustries() {
        binding.industryEditText.text?.clear()
        binding.industryClear.visibility = View.GONE
        binding.industryButton.visibility = View.VISIBLE
        viewModel.clearIndustries()
    }

    private fun showView(state: ShowViewState) {
        when (state) {
            is ShowViewState.showClearIcon -> binding.clearIcon.visibility = View.VISIBLE
            is ShowViewState.hideClearIcon -> binding.clearIcon.visibility = View.GONE
            is ShowViewState.showApplyButton -> binding.buttonApply.visibility = View.VISIBLE
            is ShowViewState.showClearAllButton -> binding.clearAll.visibility = View.VISIBLE
            is ShowViewState.hideClearAllButton -> binding.clearAll.visibility = View.GONE

        }
    }

    private fun clearEditTextSallary() {
        binding.salaryEditText.text?.clear()
    }

    fun doOnTextChanged() {
        binding.placeOfWork.editText!!.doOnTextChanged { inputText, _, _, _ ->
            viewModel.showAllClearButtom()
            inputTextChangeHandler.setInputStrokeColor(binding.placeOfWork, inputText)

        }
        binding.industry.editText!!.doOnTextChanged { inputText, _, _, _ ->
            viewModel.showAllClearButtom()
            inputTextChangeHandler.setInputStrokeColor(binding.industry, inputText)

        }
        binding.salaryEditText.doOnTextChanged{inputText, _, _, _ ->
            viewModel.addSalary(inputText.toString())
            setSalaryEditTextColor(
                inputText.toString(), binding.salaryEditText.hasFocus()
            )
            viewModel.setOnFocus(
                inputText.toString(),
                binding.salaryEditText.hasFocus()
            )
        }

    }

    private fun setSalaryEditTextColor(text: CharSequence?, hasFocus: Boolean) {
        if (hasFocus) {
            binding.sallaryHint.setTextColor(resources.getColor(R.color.blue))
        }
        if (!hasFocus && !text.isNullOrEmpty()) {
            binding.sallaryHint.setTextColor(resources.getColor(R.color.black))
        }
        if (!hasFocus && text.isNullOrEmpty()) {
            binding.sallaryHint.setTextColor(resources.getColor(R.color.salary_hint_empty))
        }

    }

    companion object {
        const val SCREEN = "screen"
        const val COUNTRIES = "COUNTRIES"
        const val REGION = "REGION"
        const val INDUSTRIES = "INDUSTRIES"
    }
}