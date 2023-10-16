package ru.practicum.android.diploma.favourite.presentation.viewvodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.db.domain.api.VacancyDbInteractor
import ru.practicum.android.diploma.favourite.presentation.models.FavoriteStateInterface
import ru.practicum.android.diploma.search.domain.models.Vacancy

class FavouriteViewModel(
    private val favouriteVacancyDbInteractor: VacancyDbInteractor,
) : ViewModel() {

    private val stateLiveDataFavourite = MutableLiveData<FavoriteStateInterface?>()

    fun observeStateFavourite(): LiveData<FavoriteStateInterface?> = stateLiveDataFavourite

    private fun renderStateFavourite(state: FavoriteStateInterface) {
        stateLiveDataFavourite.postValue(state)
    }

    fun showFavouriteVacancies() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                favouriteVacancyDbInteractor.getFavouriteVacancies()
                    .collect() { favouriteVacancies ->
                        if (favouriteVacancies.isEmpty())
                            renderStateFavourite(FavoriteStateInterface.FavoriteVacanciesIsEmpty)
                        else renderStateFavourite(
                            FavoriteStateInterface.FavoriteVacancies(favouriteVacancies)
                        )
                    }
            }
        }
    }

    fun pause(){
        stateLiveDataFavourite.postValue(null)
    }

    fun deleteVacancy(vacancy: Vacancy) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                favouriteVacancyDbInteractor.getFavouriteVacancyById(vacancy.id).collect() {
                    deleteVacancyEntity(it)
                }
            }
        }
    }

    private fun deleteVacancyEntity(vacancy: Vacancy?) {
        if (vacancy == null) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                favouriteVacancyDbInteractor.deleteFavouriteVacancyById(vacancy.id)
                }
            }
        }
    }