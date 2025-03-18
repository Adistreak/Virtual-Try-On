package com.hisuperaman.cartit.data.database

import com.hisuperaman.cartit.data.model.Cart
import com.hisuperaman.cartit.data.model.CartItem
import com.hisuperaman.cartit.data.model.Product
import kotlinx.coroutines.flow.Flow

class CartItemRepository(private val cartItemDao: CartItemDao) {
    suspend fun addCartItem(cartItem: CartItem) = cartItemDao.addCartItem(cartItem)
    suspend fun deleteCartItem(cartItemId: Int) = cartItemDao.deleteCartItem(cartItemId)

    suspend fun updateQuantity(quantity: Int, cartItemId: Int) = cartItemDao.updateQuantity(quantity, cartItemId)
    suspend fun incrementQuantity(cartItemId: Int) = cartItemDao.incrementQuantity(cartItemId)
    suspend fun decrementQuantity(cartItemId: Int) = cartItemDao.decrementQuantity(cartItemId)

    fun getCartItemsWithProducts(userId: Int): Flow<List<CartItemWithProduct>> = cartItemDao.getCartItemsWithProducts(userId)

    suspend fun getAllCartItems(): List<CartItem> = cartItemDao.getAllCartItems()

    suspend fun getCartItem(cartId: Int, productId: Int): CartItem? = cartItemDao.getCartItem(cartId, productId)

    suspend fun updateCartItem(cartItem: CartItem) = cartItemDao.updateCartItem(cartItem)
}