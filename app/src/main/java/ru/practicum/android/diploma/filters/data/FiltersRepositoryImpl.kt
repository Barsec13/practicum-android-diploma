package ru.practicum.android.diploma.filters.data


import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.filters.data.dto.models.AreasDto
import ru.practicum.android.diploma.filters.data.dto.models.CountryDto
import ru.practicum.android.diploma.filters.data.dto.models.FiltersDto
import ru.practicum.android.diploma.filters.data.dto.models.IndustryDto
import ru.practicum.android.diploma.filters.domain.FiltersRepository
import ru.practicum.android.diploma.filters.domain.models.Areas
import ru.practicum.android.diploma.filters.domain.models.Country
import ru.practicum.android.diploma.filters.domain.models.Filters
import ru.practicum.android.diploma.filters.domain.models.Industries
import ru.practicum.android.diploma.filters.domain.models.Industry
import ru.practicum.android.diploma.filters.domain.models.Region
import ru.practicum.android.diploma.search.data.NetworkClient
import ru.practicum.android.diploma.search.data.ResourceProvider
import ru.practicum.android.diploma.search.data.SearchRepositoryImpl.Companion.ERROR
import ru.practicum.android.diploma.search.data.SearchRepositoryImpl.Companion.SUCCESS
import ru.practicum.android.diploma.search.data.dto.AreaSearchRequest
import ru.practicum.android.diploma.search.data.dto.IndustriesSearchRequest
import ru.practicum.android.diploma.util.Resource

class FiltersRepositoryImpl(
    private val networkClient: NetworkClient,
    private val resourceProvider: ResourceProvider,
    private val filtersStorage: FiltersStorage
) : FiltersRepository {

    override suspend fun getAreas(): Flow<Resource<List<Areas>>> = flow {
        val response = networkClient.getAres(AreaSearchRequest)
        when (response.resultCode) {
            ERROR -> {
                emit(Resource.Error(resourceProvider.getString(R.string.check_connection)))
            }

            SUCCESS -> {
                with(response) {
                    val regionList = response.resultAreas.map { mapAresFromDto(it) }
                    emit(ru.practicum.android.diploma.util.Resource.Success(moveItemToEnd(regionList.toMutableList())))
                }
            }

            else -> {
                Log.d("myLog", response.resultCode.toString())
                emit(ru.practicum.android.diploma.util.Resource.Error(resourceProvider.getString(R.string.server_error)))
            }
        }
    }

    override suspend fun getFilters(): Flow<Filters> = flow {
        emit(mapFiltersFromDto(filtersStorage.doRequest()))
    }

    override suspend fun writeFilters(filters: Filters) {
        filtersStorage.doWrite(mapFiltersDtoFromFilters(filters))
    }

    override suspend fun getIndustries(): Flow<Resource<List<Industry>>> = flow {
        val response = networkClient.getIndustries(IndustriesSearchRequest)
        when (response.resultCode) {
            ERROR -> {
                emit(Resource.Error(resourceProvider.getString(R.string.check_connection)))
            }

            SUCCESS -> {
                with(response) {
                    val industriesList = response.resultIndustries.map { mapIndustriesFromDto(it) }
                    emit(ru.practicum.android.diploma.util.Resource.Success(industriesList))
                }
            }
            else -> {

                emit(ru.practicum.android.diploma.util.Resource.Error(resourceProvider.getString(R.string.server_error)))
            }
        }
    }


    private fun mapVacancyFromDto(countryDto: CountryDto): Country {
        return Country(
            countryDto.url,
            countryDto.id,
            countryDto.name

        )
    }

    private fun mapAresFromDto(areasDto: AreasDto): Areas {
        return Areas(
            areasDto.id,
            areasDto.name,
            areasDto.areas.map {
                Region(
                    it.id,
                    it.parentId,
                    it.name
                )
            }
        )
    }

    private fun mapFiltersFromDto(filtersDto: FiltersDto): Filters {
        return Filters(
            filtersDto.countryName,
            filtersDto.countryId,
            filtersDto.areasNames,
            filtersDto.areasId,
            filtersDto.industriesName,
            filtersDto.industriesId,
            filtersDto.salary,
            filtersDto.onlyWithSalary
        )
    }

    private fun mapFiltersDtoFromFilters(filters: Filters): FiltersDto {
        return FiltersDto(
            filters.countryName,
            filters.countryId,
            filters.areasNames,
            filters.areasId,
            filters.industriesName,
            filters.industriesId,
            filters.salary,
            filters.onlyWithSalary
        )
    }

    private fun mapIndustriesFromDto(industries: IndustryDto): Industry {
        return Industry(
            industries.id,
            industries.industries.map {
                Industries(
                    it.id,
                    it.name
                )
            },
            industries.name

        )
    }
    fun  moveItemToEnd(list: MutableList<Areas>):MutableList<Areas> {
        var area:Areas? = null
        list.map { if(it.name.equals(resourceProvider.getString(R.string.another_region)))area = it }
        val index = list.indexOf(area)
        if (index >= 0 && index < list.size) {
            val item = list.removeAt(index)
            list.add(item)
        }
        return list
    }

}