package com.app.deliveryapplication.vo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String = "",
    val product_title: String? = "",
    val product_category: String? = "",
    val product_subcategory: String? = "",
    val current_price: String? = "",
    val default_price: String? = "",
    val img_url: String? = ""
)




