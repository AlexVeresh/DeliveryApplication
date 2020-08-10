package com.app.deliveryapplication.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.app.deliveryapplication.vo.*

@Database(entities = [
    Product::class,
    Order::class,
    BasketItem::class,
    Restaurant::class,
    Dish::class
], version = 1, exportSchema = false)
@TypeConverters(AppTypeConverters::class)
abstract class DeliveryDb: RoomDatabase() {
    abstract fun products(): ProductsDao
    abstract fun orders(): OrdersDao
    abstract fun search(): SearchDao
    abstract fun restaurants(): RestaurantsDao
}
