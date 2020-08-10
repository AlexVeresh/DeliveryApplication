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
import java.net.HttpURLConnection


@OptIn(ExperimentalPagingApi::class)
class ProductsRemoteMediator (
    private val db: DeliveryDb,
    private val remoteApi: RemoteStorageApi,
    private val category: String?,
    private val subcategory: String?
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

            val items = remoteApi.loadProducts(
                collection = "Products",
                limitSize = when(loadType){
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                },
                after = loadKey,
                category = category,
                subcategory = subcategory
            )

            db.withTransaction {
                if(loadType == LoadType.REFRESH){

                    if (subcategory == null) {
                        products.deleteByCategory(category)
                    } else {
                        products.deleteBySubcategory(subcategory)
                    }
                }
                products.insertProducts(items)
            }

            MediatorResult.Success(endOfPaginationReached = items.isEmpty())

        } catch (e: IOException) {
            MediatorResult.Error(e)

        } catch (e: Exception){
            MediatorResult.Error(e)
        }
    }

}