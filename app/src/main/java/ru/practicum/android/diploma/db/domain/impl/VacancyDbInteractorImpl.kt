package ru.practicum.android.diploma.db.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.db.data.converter.VacancyDbConverter
import ru.practicum.android.diploma.db.domain.api.VacancyDbInteractor
import ru.practicum.android.diploma.db.domain.api.VacancyDbRepository
import ru.practicum.android.diploma.details.domain.models.VacancyDetails
import ru.practicum.android.diploma.search.domain.models.Vacancy

class VacancyDbInteractorImpl(
    private val vacancyDbRepository: VacancyDbRepository,
    private val vacancyDbConverter: VacancyDbConverter,
) : VacancyDbInteractor {
    override suspend fun insertFavouriteVacancy(vacancy: Vacancy, vacancyDetails: VacancyDetails) {
        vacancyDbRepository.insertFavouriteVacancy(vacancyDbConverter.map(vacancy, vacancyDetails))
    }

    override suspend fun getFavouriteVacancies(): Flow<List<Vacancy>> {
        return vacancyDbRepository.getFavouriteVacancies()
    }

    override suspend fun getFavouriteVacancyDetailsById(vacancyId: String): Flow<VacancyDetails?> {
        return vacancyDbRepository.getFavouriteVacancyDetailsById(vacancyId)
    }

    override suspend fun getFavouriteVacancyById(vacancyId: String): Flow<Vacancy?> {
        return vacancyDbRepository.getFavouriteVacancyById(vacancyId)
    }

    override suspend fun deleteFavouriteVacancyById(vacancyId: String){
        vacancyDbRepository.deleteFavouriteVacancyById(vacancyId)
    }
}