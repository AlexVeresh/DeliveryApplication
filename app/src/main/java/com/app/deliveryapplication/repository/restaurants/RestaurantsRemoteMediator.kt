package com.app.deliveryapplication.repository.restaurants

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.app.deliveryapplication.api.RemoteStorageApi
import com.app.deliveryapplication.db.DeliveryDb
import com.app.deliveryapplication.vo.Product
import com.app.deliveryapplication.vo.Restaurant
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class RestaurantsRemoteMediator (
    private val db: DeliveryDb,
    private val remoteApi: RemoteStorageApi
):RemoteMediator<Int, Restaurant>(){
    val restaurants = db.restaurants()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Restaurant>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType){
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()

                    if (lastItem == null){
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    lastItem.restaurant_id
                }
            }
            val items = remoteApi.loadRestaurants(
                collection = "Restaurants",
                limitSize = when(loadType){
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                },
                after = loadKey
            )

            db.withTransaction {
                if(loadType == LoadType.REFRESH){
                    restaurants.deleteRestaurants()
                }

                restaurants.insertRestaurants(items)


            }
            MediatorResult.Success(endOfPaginationReached = items.isEmpty())

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

}