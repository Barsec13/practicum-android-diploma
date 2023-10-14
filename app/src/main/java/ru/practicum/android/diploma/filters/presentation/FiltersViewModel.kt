package ru.practicum.android.diploma.filters.presentation

import android.util.Log
import android.view.View
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
import ru.practicum.android.diploma.filters.ui.fragment.SettingFiltersFragment.Companion.COUNTRIES
import ru.practicum.android.diploma.filters.ui.fragment.SettingFiltersFragment.Companion.INDUSTRIES
import ru.practicum.android.diploma.filters.ui.fragment.SettingFiltersFragment.Companion.REGION
import ru.practicum.android.diploma.util.app.App

class FiltersViewModel(
    val filtersInteractor: FiltersInteractor,
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
                    regionList.addAll(areasList.flatMap { it.areas })
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
                    industries.addAll(industryList.flatMap { it.industries })
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
        if (hasFocus && editText!!.isNotEmpty()) showViewState.postValue(
            ShowViewState.showClearIcon
        )
    }


    fun addCountry(country: Areas) {
        filtersNew.countryName = country.name
        parentId = country.id
        filtersNew.countryId = parentId
        App.DATA_HAS_CHANGED = "yes"
        writeFilters()

    }

    fun addSalary(query: String) {
        if(query.isEmpty()){ filtersNew.salary = 0 }else{ filtersNew.salary = query.toInt() }
        showAllClearButtom()
        hasDataChanged()
        writeFilters()
        Log.d("salary", filtersNew.salary.toString())
    }


    fun addArea(region: Region) {
        filtersNew.areasId = region.id
        filtersNew.areasNames = region.name
        filtersNew.countryId = region.parentId
        countries.map { if (it.id.equals(filtersNew.countryId)) addCountry(it) }
        writeFilters()
    }

    fun addIndustries(industries: Industries) {
        filtersNew.industriesId = industries.id
        filtersNew.industriesName = industries.name
        App.DATA_HAS_CHANGED = "yes"
        writeFilters()
    }

    fun addOnlyWithSalary(withSalary: Boolean) {
        showAllClearButtom()
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
                    filtersNew.industriesId = filters.industriesId
                    filtersNew.salary = filters.salary
                    filtersNew.onlyWithSalary = filters.onlyWithSalary
                    lastSallary = filters.salary.toString()
                }
        }
    }

    fun writeFilters() {
        writeFiltersJob = viewModelScope.launch {
            filtersInteractor.writeFilters(filtersNew)
            showViewState.postValue(ShowViewState.showApplyButton)
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
        App.DATA_HAS_CHANGED = "no"
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
        App.DATA_HAS_CHANGED = "no"
    }

    fun searchIndustry(searchTerm: String?,isChecked:Boolean ) {
        val foundIndustriesList = mutableListOf<Industries>()
        foundIndustriesList.clear()
        if (searchTerm.isNullOrEmpty()&&isChecked!=true) {
            screenStateLiveData.postValue(ScreenState.ShowIndustryList(newIndustries))
        } else {
            searchTerm?.let { if(it.isNotEmpty())newIndustries.map { if(it.name.contains(searchTerm, ignoreCase = true))
                foundIndustriesList.add(it)
                screenStateLiveData.postValue(ScreenState.ShowIndustryList(foundIndustriesList))
            } }
        }


    }
    fun searchRegion(searchTerm: String?, isChecked:Boolean) {
        val foundRegionList = mutableListOf<Region>()
        foundRegionList.clear()
        if (searchTerm.isNullOrEmpty()&&isChecked!=true) {
            screenStateLiveData.postValue(ScreenState.ShowAreasList(region))
        }
            searchTerm?.let {if(it.isNotEmpty())region.map { if(it.name.contains(searchTerm, ignoreCase = true))
                foundRegionList.add(it)
                screenStateLiveData.postValue(ScreenState.ShowAreasList(foundRegionList))
            } }
    }

    fun hasDataChanged() {
        viewModelScope.launch {
            delay(100)
            if (App.DATA_HAS_CHANGED != "no" || lastSallary != filtersNew.salary.toString()) {
                showViewState.postValue(ShowViewState.showApplyButton)
            }

        }
    }

    fun showAllClearButtom() {
        viewModelScope.launch {
            delay(50)
            if (filtersNew.salary != 0 || !filtersNew.countryName.isNullOrEmpty() || !filtersNew.industriesName.isNullOrEmpty() || filtersNew.onlyWithSalary != false) {
                showViewState.postValue(ShowViewState.showClearAllButton)
            } else {
                showViewState.postValue(ShowViewState.hideClearAllButton)
            }
        }
    }


}