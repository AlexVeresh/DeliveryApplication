package com.app.deliveryapplication.db

import androidx.paging.PagingSource
import androidx.room.*
import com.app.deliveryapplication.vo.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(list: List<Product>)

    @Query("SELECT * FROM products")
    fun getAllProducts() : PagingSource<Int, Product>

    @Query("SELECT * FROM products WHERE product_category = :product_category")
    fun getProductsByCategory(product_category: String?): PagingSource<Int, Product>

    @Query("SELECT * FROM products WHERE product_subcategory = :product_subcategory")
    fun getProductsBySubcategory(product_subcategory: String): PagingSource<Int, Product>

    @Query("SELECT * FROM products WHERE id = :product_id")
    fun getProductById(product_id: String): Flow<Product>

    @Query("SELECT * FROM products WHERE current_price != default_price")
    fun getPromoProducts():PagingSource<Int, Product>

    @Query("DELETE FROM products WHERE product_category = :product_category")
    suspend fun deleteByCategory(product_category: String?)

    @Query("DELETE FROM products WHERE product_subcategory = :product_subcategory")
    suspend fun deleteBySubcategory(product_subcategory: String)

    @Query("DELETE FROM products WHERE current_price != default_price")
    suspend fun deletePromoItems()

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