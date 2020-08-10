package com.app.deliveryapplication.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.deliveryapplication.vo.BasketItem
import com.app.deliveryapplication.vo.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasketItem(basketItem: BasketItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasketItems(basketItems: List<BasketItem>)

    @Query("SELECT * FROM basket_items")
    fun getAllBasketItems(): Flow<List<BasketItem>>

    @Query("SELECT * FROM orders")
    fun getOrders():Flow<List<Order>>

    @Query("UPDATE basket_items SET amount = :amount WHERE basketItemId = :basketItemId")
    suspend fun updateBasketItemAmount(amount: Int, basketItemId: String): Int

    @Query("SELECT * FROM basket_items WHERE basketItemId= :basketItemId")
    fun getBasketItemById(basketItemId: String): BasketItem?

    @Query("DELETE FROM basket_items WHERE basketItemId= :basketItemId")
    suspend fun deleteBasketItemById(basketItemId: String)

    @Query("DELETE FROM basket_items")
    suspend fun deleteAllBasketItems()

    @Query("DELETE FROM orders WHERE orderId = :orderId")
    suspend fun deleteOrderById(orderId: Int)
}