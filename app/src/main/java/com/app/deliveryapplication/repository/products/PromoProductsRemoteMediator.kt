package com.app.deliveryapplication.repository.products

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.app.deliveryapplication.api.RemoteStorageApi
import com.app.deliveryapplication.db.DeliveryDb
import com.app.deliveryapplication.vo.Product
import java.io.IOException


@OptIn(ExperimentalPagingApi::class)
class PromoProductsRemoteMediator (
    private val db: DeliveryDb,
    private val remoteApi: RemoteStorageApi
) : RemoteMediator<Int, Product>() {
    val products = db.products()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Product>
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

                    lastItem.id
                }
            }
            val items = remoteApi.loadPromoProducts(
                limitSize = when(loadType){
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                },
                after = loadKey
            )
            db.withTransaction {
                if(loadType == LoadType.REFRESH){
                    db.products().deletePromoItems()
                }
                products.insertProducts(items)
            }

            MediatorResult.Success(endOfPaginationReached = items.isEmpty())

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

}