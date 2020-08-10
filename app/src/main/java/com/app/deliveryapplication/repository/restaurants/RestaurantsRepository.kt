package com.app.deliveryapplication.repository.restaurants

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.app.deliveryapplication.api.RemoteStorageApi
import com.app.deliveryapplication.db.DeliveryDb
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantsRepository @Inject constructor(
    private val db: DeliveryDb,
    private val remoteStorage: RemoteStorageApi
) {

    fun getRestaurants(pageSize: Int) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = RestaurantsRemoteMediator(db, remoteStorage)

    ) {
        db.restaurants().getRestaurants()
    }.flow

    fun getRestaurantById(restaurantId: String) = db.restaurants().getRestaurantById(restaurantId)

}