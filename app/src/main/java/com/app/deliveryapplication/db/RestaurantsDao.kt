package com.app.deliveryapplication.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.deliveryapplication.vo.Dish
import com.app.deliveryapplication.vo.Restaurant
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurants(list: List<Restaurant>)

    @Query("SELECT * FROM restaurants")
    fun getRestaurants() : PagingSource<Int, Restaurant>

    @Query("DELETE FROM restaurants")
    suspend fun deleteRestaurants()

    @Query("SELECT * FROM dishes WHERE restaurant_owner_id = :restaurant_id")
    fun getDishesById(restaurant_id: String):Flow<List<Dish>>

    @Query("SELECT * FROM dishes WHERE dish_id = :dish_id")
    fun getDishById(dish_id: String): Flow<Dish>

    @Query("SELECT * FROM restaurants WHERE restaurant_id = :restaurant_id")
    fun getRestaurantById(restaurant_id: String): Flow<Restaurant>

    @Query("DELETE FROM dishes WHERE restaurant_owner_id =:restaurant_id")
    suspend fun deleteDishesById(restaurant_id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDishes(list: List<Dish>?)

}