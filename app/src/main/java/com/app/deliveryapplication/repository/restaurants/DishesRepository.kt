package com.app.deliveryapplication.repository.restaurants

import androidx.room.withTransaction
import com.app.deliveryapplication.api.RemoteStorageApi
import com.app.deliveryapplication.db.DeliveryDb
import com.app.deliveryapplication.vo.Dish
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DishesRepository @Inject constructor(
    private val db: DeliveryDb,
    private val remoteStorageApi: RemoteStorageApi
){
    suspend fun dishesById(restaurantId: String): Flow<List<Dish>> {
        loadData(restaurantId)
        return db.restaurants().getDishesById(restaurantId)
    }

    suspend fun loadData(restaurantId: String){
        try {
            val dishItems = remoteStorageApi.loadDishesById(restaurantId, "Dishes")
            db.withTransaction {
                db.restaurants().deleteDishesById(restaurantId)
                db.restaurants().insertDishes(dishItems)
            }
        }
        catch (e: Exception){
            db.withTransaction {
                db.restaurants().insertDishes(emptyList())
            }
        }
    }

    fun getDishById(dishId: String) = db.restaurants().getDishById(dishId)



}