package ru.practicum.android.diploma.details.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.db.domain.api.VacancyDbInteractor
import ru.practicum.android.diploma.details.domain.VacancyInteractor
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.search.data.ResourceProvider
import ru.practicum.android.diploma.search.domain.models.Vacancy

class VacancyViewModel(
    private val vacancyInteractor: VacancyInteractor,
    private val resourceProvider: ResourceProvider,
    private val vacancyDbInteractor: VacancyDbInteractor,
): ViewModel() {

    private val _state = MutableLiveData<VacancyState>()
    val state: LiveData<VacancyState> = _state

    private val _stateFavouriteIconLiveData = MutableLiveData<Boolean>()
    val stateFavouriteIconLiveData: LiveData<Boolean> = _stateFavouriteIconLiveData

    private val _stateVacancyInfoDb = MutableLiveData<VacancyDetails?>()
    val stateVacancyInfoDb: LiveData<VacancyDetails?> = _stateVacancyInfoDb

    private var isFavourite = false

    fun loadVacancyDetails(vacancyId: String) {
        _state.postValue(VacancyState.Loading)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = vacancyInteractor.loadVacancyDetails(vacancyId)
                processResult(result.first, result.second)
            }
        }
    }

    private fun processResult(vacancyDetails: VacancyDetails?, errorMessage: String?) {
        when {
            errorMessage != null ->
                _state.postValue(VacancyState.Error(resourceProvider.getString(R.string.no_connection)))

            else ->
                _state.postValue(VacancyState.Content(vacancyDetails!!))
        }
    }

    fun clickOnFavoriteIcon(vacancy: Vacancy, vacancyDetails: VacancyDetails) {
        if (isFavourite) {
            _stateFavouriteIconLiveData.postValue(false)
            isFavourite = false
            deleteVacancy(vacancy.id)
        } else {
            _stateFavouriteIconLiveData.postValue(true)
            isFavourite = true
            insertVacancy(vacancy, vacancyDetails)
        }
    }

    private fun insertVacancy(vacancy: Vacancy, vacancyDetails: VacancyDetails) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                vacancyDbInteractor.insertFavouriteVacancy(vacancy, vacancyDetails)
            }
        }
    }

    fun checkFavourite(vacancy: Vacancy) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                vacancyDbInteractor.getFavouriteVacancies().collect() { favouriteVacancies ->
                    favouriteVacancies.forEach { favouriteVacancy ->
                        if (vacancy.id == favouriteVacancy.id) isFavourite = true
                    }
                    _stateFavouriteIconLiveData.postValue(isFavourite)
                }
            }
        }
    }

    fun shareVacancyUrl(vacancyUrl: String){
        vacancyInteractor.shareVacancyUrl(vacancyUrl)
    }

    fun sharePhone(phone: String) {
        vacancyInteractor.sharePhone(phone)
    }

    fun shareEmail(email: String) {
        vacancyInteractor.shareEmail(email)
    }

    fun initVacancyDetailsInDb(vacancy: Vacancy) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                vacancyDbInteractor.getFavouriteVacancyDetailsById(vacancy.id)
                    .collect() { vacancyDetails ->
                        renderStateVacancyInfoDb(vacancyDetails)
                    }
            }
        }
    }

    private fun renderStateVacancyInfoDb(vacancyDetails: VacancyDetails?) {
        if (vacancyDetails == null)
            _stateVacancyInfoDb.postValue(null)
        else
            _stateVacancyInfoDb.postValue(vacancyDetails)
    }

    private fun deleteVacancy(vacancyId: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                vacancyDbInteractor.deleteFavouriteVacancyById(vacancyId)
            }
        }
    }
}