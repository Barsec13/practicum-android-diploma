package ru.practicum.android.diploma.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.db.data.entity.VacancyEntity

@Dao
interface VacancyDao {
    @Insert(entity = VacancyEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteVacancy(vacancyEntity: VacancyEntity)

    @Query("SELECT * FROM vacancy_table")
    fun getFavouriteVacancies(): Flow<List<VacancyEntity>>

    @Query("SELECT * FROM vacancy_table WHERE id = :vacancyId")
    fun getFavouriteVacancyById(vacancyId: String): Flow<VacancyEntity>

    @Query("DELETE FROM vacancy_table WHERE id = :vacancyId")
    fun deleteFavouriteVacancyById(vacancyId: String)
}