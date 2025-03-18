package com.hisuperaman.cartit.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hisuperaman.cartit.data.model.Product
import com.hisuperaman.cartit.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert
    suspend fun addProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProducts(product: List<Product>)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Query("SELECT * FROM products where id = :productId")
    suspend fun getProductById(productId: Int): Product

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>
}