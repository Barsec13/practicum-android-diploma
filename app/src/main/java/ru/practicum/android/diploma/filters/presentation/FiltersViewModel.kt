package ru.practicum.android.diploma.filters.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filters.domain.FiltersInteractor
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.filters.domain.models.Industries
import ru.practicum.android.diploma.filters.domain.models.Industry
import ru.practicum.android.diploma.filters.domain.models.Region
import ru.practicum.android.diploma.filters.presentation.models.FiltersDataState
import ru.practicum.android.diploma.filters.presentation.models.ScreenState
import ru.practicum.android.diploma.filters.presentation.models.ShowViewState
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.COUNTRIES
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.INDUSTRIES
import ru.practicum.android.diploma.filters.ui.fragment.FragmentSettingFilters.Companion.REGION
import ru.practicum.android.diploma.search.data.ResourceProvider
import ru.practicum.android.diploma.util.app.App

class FiltersViewModel(
    val filtersInteractor: FiltersInteractor,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<ScreenState>()
    private val filtersDataStateLiveData = MutableLiveData<FiltersDataState>()
    private var showViewState = MutableLiveData<ShowViewState>()
    private var getAreasJob: Job? = null
    private var getFiltersJob: Job? = null
    private var getIndustriesJob: Job? = null
    private var showFiltersData: Job? = null
    private var writeFiltersJob: Job? = null
    private var countries = mutableListOf<Areas>()
    private var newIndustryList = mutableListOf<Industry>()
    private var newIndustries = mutableListOf<Industries>()
    private var region = mutableListOf<Region>()
    private var parentId: String? = null
    private var lastSallary: String? = null
    private var filtersNew: Filters =
        Filters(
            countryName = null,
            countryId = null,
            areasNames = null,
            areasId = null,
            industriesName = null,
            industriesId = null,
            salary = 0,
            onlyWithSalary = false
        )

    fun getScreenStateLiveData(): LiveData<ScreenState> = screenStateLiveData
    fun getFiltersStateLiveData(): LiveData<FiltersDataState> = filtersDataStateLiveData
    fun getShowViewStateLiveData(): LiveData<ShowViewState> = showViewState


    init {
        getFilters()
    }

    fun setScreen(nameOfScreen: String) {
        when (nameOfScreen) {
            COUNTRIES -> setScreenCountries()
            REGION -> setScreenRegion()
            INDUSTRIES -> setScreenIndustries()
        }
    }

    private fun setScreenCountries() {
        getAreasJob = viewModelScope.launch {
            screenStateLiveData.postValue(ScreenState.ShowCountriesScreen)
            getAreas()
            screenStateLiveData.postValue(ScreenState.ShowCountriesList(countries))
        }
    }

    private fun setScreenIndustries() {
        getIndustriesJob = viewModelScope.launch {
            screenStateLiveData.postValue(ScreenState.ShowIndustriesScreen)
            getIndustries()
            screenStateLiveData.postValue(ScreenState.ShowIndustryList(newIndustries))
        }
    }

    private fun setScreenRegion() {
        if (filtersNew.countryId.isNullOrEmpty()) {
            getAreasJob = viewModelScope.launch {
                screenStateLiveData.postValue(ScreenState.ShowAreasScreen)
                getAreas()
                screenStateLiveData.postValue(ScreenState.ShowAreasList(region))
            }
        } else {
            getAreasJob = viewModelScope.launch {
                screenStateLiveData.postValue(ScreenState.ShowAreasScreen)
                getAreas()
                region.clear()
                countries.map { if (it.id.equals(filtersNew.countryId)) region.addAll(it.areas) }
                screenStateLiveData.postValue(ScreenState.ShowAreasList(region))
            }
        }
    }

    suspend fun getAreas() {
        filtersInteractor.getAreas()
            .collect { results ->
                val areasList = mutableListOf<Areas>()
                val regionList = mutableListOf<Region>()
                if (results.data != null) {
                    areasList.addAll(results.data)
                    areasList.map { it.areas.map { regionList.add(it) } }
                }
                when {
                    results.message != null -> {
                        Log.d("myLog", results.message.toString())
                    }

                    areasList.isEmpty() -> {
                        Log.d("myLog", "Пустой список")
                    }

                    else -> {
                        countries.addAll(areasList)
                        region.addAll(regionList)
                    }
                }
            }
    }

    suspend fun getIndustries() {
        filtersInteractor.getIndustries()
            .collect { results ->
                val industryList = mutableListOf<Industry>()
                val industries = mutableListOf<Industries>()
                if (results.data != null) {
                    industryList.addAll(results.data)
                    industryList.map { it.industries.map { industries.add(it) } }
                }
                when {
                    results.message != null -> {

                    }

                    industryList.isEmpty() -> {

                    }

                    else -> {
                        newIndustryList.addAll(industryList)
                        newIndustries.addAll(industries)
                    }
                }
            }
    }

    fun setOnFocus(editText: String?, hasFocus: Boolean) {
        if (hasFocus && editText!!.isEmpty()) showViewState.postValue(ShowViewState.hideClearIcon)
        if (hasFocus && editText!!.isNotEmpty() && editText != "Введите сумму") showViewState.postValue(
            ShowViewState.showClearIcon
        )
        if (hasFocus && editText!!.isNotEmpty() && editText.equals("Введите сумму")) showViewState.postValue(
            ShowViewState.clearEditText
        )
    }


    fun addCountry(country: Areas) {
        filtersNew.countryName = country.name
        parentId = country.id
        filtersNew.countryId = parentId
        App.DADA_HAS_CHANGHED = "yes"
        writeFilters()

    }

    fun addSalary(query: String) {

            if (query != "Введите сумму") {
                query.takeIf { it.isNotEmpty() }?.let { filtersNew.salary = query.toInt() }
                showAllClearButtom(query)
                hasDataChanged()
                writeFilters()
            } else {
                filtersNew.salary = 0
                writeFilters()
            }
            Log.d("salary", filtersNew.salary.toString())

    }


    fun addArea(region: Region) {
        filtersNew.areasId = region.id
        filtersNew.areasNames = region.name
        filtersNew.countryId = region.parent_id
        countries.map { if (it.id.equals(filtersNew.countryId)) addCountry(it) }
        writeFilters()
    }

    fun addIndustries(industries: Industries) {
        filtersNew.industriesId = industries.id
        filtersNew.industriesName = industries.name
        App.DADA_HAS_CHANGHED = "yes"
        writeFilters()
    }

    fun addOnlyWithSalary(withSalary: Boolean) {
        filtersNew.onlyWithSalary = withSalary
        writeFilters()
    }

    private fun getFilters() {
        getFiltersJob = viewModelScope.launch {
            filtersInteractor.getFilters()
                ?.collect { filters ->
                    filtersNew.countryName = filters.countryName
                    filtersNew.areasId = filters.areasId
                    filtersNew.areasNames = filters.areasNames
                    filtersNew.countryId = filters.countryId
                    filtersNew.industriesName = filters.industriesName
                    filtersNew.salary = filters.salary
                    filtersNew.onlyWithSalary = filters.onlyWithSalary
                    lastSallary = filters.salary.toString()
                }
        }
    }

    fun writeFilters() {
        writeFiltersJob = viewModelScope.launch {
            filtersInteractor.writeFilters(filtersNew)
        }
    }

    fun showFiltersData() {
        showFiltersData = viewModelScope.launch {
            getFilters()
            filtersDataStateLiveData.postValue(FiltersDataState.filtersData(filtersNew))
        }
    }

    fun clearCountry() {
        filtersNew.countryName = null
        filtersNew.countryId = null
        writeFilters()
        App.DADA_HAS_CHANGHED = "no"
    }

    fun clearRegion() {
        filtersNew.areasNames = null
        filtersNew.areasId = null
        writeFilters()
    }

    fun clearIndustries() {
        filtersNew.industriesName = null
        filtersNew.industriesId = null
        writeFilters()
        App.DADA_HAS_CHANGHED = "no"
    }

    fun searchIndustry(searchTerm: String?) {
        val foundIndustriesList = mutableListOf<Industries>()
        foundIndustriesList.clear()
        if (searchTerm.isNullOrEmpty()) {
            screenStateLiveData.postValue(ScreenState.ShowIndustryList(newIndustries))
        } else {
            newIndustries.map {
                if (it.name.contains(
                        searchTerm,
                        ignoreCase = true
                    )
                ) foundIndustriesList.add(it)
            }
            screenStateLiveData.postValue(ScreenState.ShowIndustryList(foundIndustriesList))
        }


    }

    fun searchRegion(searchTerm: String?) {
        val foundRegionList = mutableListOf<Region>()
        foundRegionList.clear()
        if (searchTerm.isNullOrEmpty()) {
            screenStateLiveData.postValue(ScreenState.ShowAreasList(region))

        } else {
            region.map {
                if (it.name.contains(
                        searchTerm,
                        ignoreCase = true
                    )
                ) foundRegionList.add(it)
            }
            screenStateLiveData.postValue(ScreenState.ShowAreasList(foundRegionList))
        }
    }

    fun hasDataChanged() {
        viewModelScope.launch {
            delay(100)
            if (App.DADA_HAS_CHANGHED != "no" || lastSallary != filtersNew.salary.toString()) {
                showViewState.postValue(ShowViewState.showApplyButton)
            }

        }
    }
    fun showAllClearButtom(text:String){
        viewModelScope.launch {
            delay(50)
            if(text!="0") {
                showViewState.postValue(ShowViewState.showClearAllButton)
            }
        }
    }



}