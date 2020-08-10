package com.app.deliveryapplication.vo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey val restaurant_id: String = "",
    val restaurant_title: String = "",
    val restaurant_price: Int = 0,
    val restaurant_rating: String = "",
    val restaurant_description: String = "",
    val restaurant_delivery_time: String ="",
    val restaurant_img_url: String = ""
)
