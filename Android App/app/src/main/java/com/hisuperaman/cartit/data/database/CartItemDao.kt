package com.hisuperaman.cartit.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hisuperaman.cartit.data.model.Cart
import com.hisuperaman.cartit.data.model.CartItem
import com.hisuperaman.cartit.data.model.Product
import com.hisuperaman.cartit.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {
    @Insert
    suspend fun addCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items WHERE id=:cartItemId")
    suspend fun deleteCartItem(cartItemId: Int)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :cartItemId")
    suspend fun updateQuantity(quantity: Int, cartItemId: Int)

    @Query("UPDATE cart_items SET quantity = quantity + 1 WHERE id = :cartItemId")
    suspend fun incrementQuantity(cartItemId: Int)

    @Query("UPDATE cart_items SET quantity = CASE WHEN quantity > 0 THEN quantity - 1 ELSE 0 END WHERE id=:cartItemId")
    suspend fun decrementQuantity(cartItemId: Int)

    @Query("""
        SELECT p.*, ci.id AS cartItemId, ci.quantity FROM cart_items ci
        INNER JOIN products p ON ci.productId = p.id
        WHERE ci.cartId = (SELECT id FROM carts WHERE userId = :userId LIMIT 1)
        ORDER BY ci.createdAt DESC
    """)
    fun getCartItemsWithProducts(userId: Int): Flow<List<CartItemWithProduct>>

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId and productId = :productId")
    suspend fun getCartItem(cartId: Int, productId: Int): CartItem?

    @Query("SELECT * FROM cart_items")
    suspend fun getAllCartItems(): List<CartItem>

    @Update
    suspend fun updateCartItem(cartItem: CartItem)
}

data class CartItemWithProduct(
    @Embedded val product: Product,
    val cartItemId: Int,
    val quantity: Int
)