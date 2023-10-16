package ru.practicum.android.diploma.db.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.db.data.entity.VacancyEntity
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.search.domain.models.Vacancy

interface VacancyDbRepository {
    suspend fun insertFavouriteVacancy(vacancyEntity: VacancyEntity)
    suspend fun getFavouriteVacancies(): Flow<List<Vacancy>>
    suspend fun getFavouriteVacancyDetailsById(vacancyId: String): Flow<VacancyDetails?>
    suspend fun getFavouriteVacancyById(vacancyId: String): Flow<Vacancy?>
    suspend fun deleteFavouriteVacancyById(vacancyId: String)
}