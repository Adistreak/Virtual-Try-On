package com.hisuperaman.cartit.data.database

import com.hisuperaman.cartit.data.model.Cart
import com.hisuperaman.cartit.data.model.Product
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {
    suspend fun addCart(cart: Cart) = cartDao.addCart(cart)
    suspend fun updateCartIncrement(quantity: Int, totalPrice: Long, userId: Int) = cartDao.updateCartIncrement(quantity, totalPrice, userId)
    suspend fun updateCartDecrement(quantity: Int, totalPrice: Long, userId: Int) = cartDao.updateCartDecrement(quantity, totalPrice, userId)
    fun getCart(userId: Int): Flow<Cart> = cartDao.getCart(userId)
}