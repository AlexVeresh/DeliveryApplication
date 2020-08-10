package com.app.deliveryapplication.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.deliveryapplication.vo.Product
@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(list: List<Product>)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query(
        "SELECT * FROM products WHERE product_title LIKE '%' || :searchingItem  || '%'" +
                "OR product_category LIKE '%' || :searchingItem  || '%'" +
                "OR product_subcategory LIKE '%' || :searchingItem  || '%'" +
                "OR current_price LIKE '%' || :searchingItem  || '%'" +
                "OR default_price LIKE '%' || :searchingItem  || '%'"
    )
    fun searchForItem(searchingItem: String): PagingSource<Int, Product>
}