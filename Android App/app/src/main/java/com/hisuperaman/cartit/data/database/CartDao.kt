package com.hisuperaman.cartit.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hisuperaman.cartit.data.model.Cart
import com.hisuperaman.cartit.data.model.Product
import com.hisuperaman.cartit.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert
    suspend fun addCart(cart: Cart)

    @Query("UPDATE carts SET totalItems = totalItems + :quantity , totalPrice = totalPrice + :totalPrice WHERE userId = :userId")
    suspend fun updateCartIncrement(quantity: Int, totalPrice: Long, userId: Int)

    @Query("""
        UPDATE carts 
        SET 
            totalItems = CASE 
                            WHEN totalItems - :quantity < 0 THEN 0 
                            ELSE totalItems - :quantity 
                         END,
            totalPrice = CASE 
                            WHEN totalPrice - :totalPrice < 0 THEN 0 
                            ELSE totalPrice - :totalPrice 
                         END
        WHERE userId = :userId
""")
    suspend fun updateCartDecrement(quantity: Int, totalPrice: Long, userId: Int)


    @Query("SELECT * FROM carts WHERE userId = :userId")
    fun getCart(userId: Int): Flow<Cart>
}