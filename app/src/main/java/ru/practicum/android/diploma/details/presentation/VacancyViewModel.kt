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
    private val vacancyDbInteractor: VacancyDbInteractor
): ViewModel() {

    private val _state = MutableLiveData<VacancyState>()
    val state: LiveData<VacancyState> = _state

    private val stateFavouriteIconLiveData = MutableLiveData<Boolean>()
    fun observeStateFavouriteIcon(): LiveData<Boolean> = stateFavouriteIconLiveData

    fun loadVacancyDetails(vacancyId: String){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                val result = vacancyInteractor.loadVacancyDetails(vacancyId)
                processResult(result.first, result.second)
            }
        }
    }

    private fun processResult(vacancyDetails: VacancyDetails?, errorMessage: String?) {
        when {
            errorMessage != null -> {
                _state.postValue(VacancyState.Error(resourceProvider.getString(R.string.no_connection)))
            }

            else -> {
                _state.postValue(VacancyState.Content(vacancyDetails!!))
            }
        }
    }

    fun clickOnFavoriteIcon(vacancy: Vacancy) {
        if (stateFavouriteIconLiveData.value == true){
            stateFavouriteIconLiveData.postValue(false)
            viewModelScope.launch {
                vacancyDbInteractor.deleteVacancy(vacancy)
            }
        }

        else{
            stateFavouriteIconLiveData.postValue(true)
            viewModelScope.launch {
                vacancyDbInteractor.insertVacancy(vacancy)
            }
        }
    }

    fun checkFavourite(vacancy: Vacancy) {
        viewModelScope.launch {
            val vacancies = vacancyDbInteractor.getFavouriteVacancy()
            stateFavouriteIconLiveData.postValue(vacancy in vacancies)
        }
    }
}