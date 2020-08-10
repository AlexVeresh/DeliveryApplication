package com.app.deliveryapplication.repository.orders

import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.app.deliveryapplication.db.DeliveryDb
import com.app.deliveryapplication.vo.BasketItem
import com.app.deliveryapplication.vo.Order
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrdersRepository @Inject constructor(
    private val db: DeliveryDb
){
    suspend fun insertBasketItem(basketItem: BasketItem){
        db.withTransaction {
            db.orders().insertBasketItem(basketItem)

        }
    }
    suspend fun updateBasketItem(basketItem: BasketItem){
        db.withTransaction {
             db.orders().updateBasketItemAmount(basketItem.amount!!, basketItem.basketItemId)
        }
    }
    suspend fun getBasketItemOrNull(basketItemId: String) = db.withTransaction { db.orders().getBasketItemById(basketItemId)}

    suspend fun deleteBasketItem(basketItemId: String) = db.withTransaction { db.orders().deleteBasketItemById(basketItemId) }

    suspend fun deleteAllBasketItems() = db.withTransaction { db.orders().deleteAllBasketItems() }

    suspend fun makeOrder(order: Order){
        db.withTransaction {
            db.orders().insertOrder(order)
        }
    }

    suspend fun insertBasketItems(basketItems: List<BasketItem>) = db.withTransaction { db.orders().insertBasketItems(basketItems) }

    fun getAllOrders() = db.orders().getOrders()

    fun getAllBasketItems() = db.orders().getAllBasketItems()
}