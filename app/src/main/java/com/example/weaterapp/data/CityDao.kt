package com.example.weaterapp.data

import androidx.room.*
import com.example.weaterapp.requests.entity_requests.Favorite

@Dao
interface CityDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favorite: Favorite)

    @Delete
    fun deleteFavorite(favorite: Favorite)

    @Query("SELECT * FROM TB_FAVORITE")
    fun allFavorities(): List<Favorite>

    @Query("SELECT * FROM TB_FAVORITE WHERE id = :id")
    fun favoriteById(id: Int): List<Favorite>
}