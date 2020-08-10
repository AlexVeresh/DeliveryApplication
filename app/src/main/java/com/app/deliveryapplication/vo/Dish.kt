package com.app.deliveryapplication.vo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishes")
data class Dish(
    @PrimaryKey val dish_id: String ="",
    val dish_title: String ="",
    val dish_description: String? = "",
    val dish_price: Int = 0,
    val dish_weight: Int = 0,
    val dish_img_url: String = "",
    val restaurant_owner_id: String= ""
)