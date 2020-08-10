package com.app.deliveryapplication.db

import androidx.room.TypeConverter
import com.app.deliveryapplication.vo.BasketItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class AppTypeConverters {
    @TypeConverter
    fun listToString(basketItemsList: List<BasketItem>): String = Gson().toJson(basketItemsList)

    @TypeConverter
    fun stringToList(data: String?): List<BasketItem>{
        val dataType = object : TypeToken<List<BasketItem>>(){}.type
        return when(data){
            null -> Collections.emptyList()
            else -> Gson().fromJson<List<BasketItem>>(data, dataType)
        }

    }


}