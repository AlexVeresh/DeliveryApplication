package com.app.deliveryapplication.vo

import androidx.room.*
import com.app.deliveryapplication.db.AppTypeConverters

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,
    val address: String?,
    val promoCode: String?,
    val orderTime: String?,
    val totalPrice: Int?,
    @TypeConverters(AppTypeConverters::class)
    val basketItemList: List<BasketItem>
)

@Entity(tableName = "basket_items")
data class BasketItem(
    @PrimaryKey val basketItemId: String = "",
    @Embedded val product: Product? = null,
    @Embedded val dish: Dish? = null,
    val amount: Int? = 0
)

