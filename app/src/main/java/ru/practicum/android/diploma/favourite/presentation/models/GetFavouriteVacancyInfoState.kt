package ru.practicum.android.diploma.favourite.presentation.models

import ru.practicum.android.diploma.search.domain.models.Vacancy

interface GetFavouriteVacancyInfoState {

    object FavoriteVacanciesInfoIsEmpty :  GetFavouriteVacancyInfoState

    data class FavoriteVacanciesInfo(
        val vacancy: Vacancy,
    ) : GetFavouriteVacancyInfoState
}