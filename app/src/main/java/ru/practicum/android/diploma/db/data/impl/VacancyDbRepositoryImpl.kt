package ru.practicum.android.diploma.db.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.db.AppDataBase
import ru.practicum.android.diploma.db.data.converter.VacancyDbConverter
import ru.practicum.android.diploma.db.data.entity.VacancyEntity
import ru.practicum.android.diploma.db.domain.api.VacancyDbRepository
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.search.domain.models.Vacancy

class VacancyDbRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val vacancyDbConverter: VacancyDbConverter,
): VacancyDbRepository {
    override suspend fun insertFavouriteVacancy(vacancyEntity: VacancyEntity) {
        appDataBase.vacancyDao().insertFavouriteVacancy(vacancyEntity)
    }

    override suspend fun getFavouriteVacancies(): Flow<List<Vacancy>> {
        return appDataBase.vacancyDao().getFavouriteVacancies().map{ convertFromListVacancyEntityToListVacancy(it) }
    }

    override suspend fun getFavouriteVacancyDetailsById(vacancyId: String): Flow<VacancyDetails> {
        return appDataBase.vacancyDao().getFavouriteVacancyById(vacancyId).map { convertFromVacancyEntityToVacancyDetails(it) }
    }

    override suspend fun getFavouriteVacancyById(vacancyId: String): Flow<Vacancy> {
        return appDataBase.vacancyDao().getFavouriteVacancyById(vacancyId).map { convertFromVacancyEntityToVacancy(it) }
    }

    override suspend fun deleteFavouriteVacancyById(vacancyId: String) {
        appDataBase.vacancyDao().deleteFavouriteVacancyById(vacancyId)
    }

    private fun convertFromVacancyEntityToVacancyDetails(vacancyEntity: VacancyEntity): VacancyDetails{
        return vacancyDbConverter.mapDetail(vacancyEntity)
    }

    private fun convertFromVacancyEntityToVacancy(vacancyEntity: VacancyEntity): Vacancy{
        return vacancyDbConverter.map(vacancyEntity)
    }

    private fun convertFromListVacancyEntityToListVacancy(listVacancyEntity: List<VacancyEntity>): List<Vacancy>{
        return listVacancyEntity.map{ vacancyDbConverter.map(it) }
    }
}