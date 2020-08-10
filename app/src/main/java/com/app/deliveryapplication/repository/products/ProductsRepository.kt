package com.app.deliveryapplication.repository.products

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.app.deliveryapplication.api.RemoteStorageApi
import com.app.deliveryapplication.db.DeliveryDb
import com.app.deliveryapplication.vo.Product
import kotlinx.coroutines.flow.filter
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepository @Inject constructor(
     private val db: DeliveryDb,
     private val remoteStorage: RemoteStorageApi
) {

    fun getProducts(category: String?, subcategory: String?, pageSize: Int) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = ProductsRemoteMediator(db, remoteStorage, category, subcategory)
    ) {


           when (subcategory) {
               null -> db.products().getProductsByCategory(category)
               else -> db.products().getProductsBySubcategory(subcategory)
           }


    }.flow


    fun getPromoProducts(pageSize: Int) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = PromoProductsRemoteMediator(db, remoteStorage)
    ){
        db.products().getPromoProducts()
    }.flow


    suspend fun promoProductsForPreview() =
        try {
            remoteStorage.loadPromoProducts(8, null)
        }
        catch (e: Exception){
            emptyList<Product>()
        }

    fun getProductById(productId: String) = db.products().getProductById(productId)
}