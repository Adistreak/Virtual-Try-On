package com.hisuperaman.cartit.data.viewmodel

import android.app.Application
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisuperaman.cartit.data.database.AppDatabase
import com.hisuperaman.cartit.data.database.CartItemRepository
import com.hisuperaman.cartit.data.database.CartRepository
import com.hisuperaman.cartit.data.database.ProductRepository
import com.hisuperaman.cartit.data.database.UserRepository
import com.hisuperaman.cartit.data.model.CartItem
import com.hisuperaman.cartit.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import android.util.Log
import com.hisuperaman.cartit.data.database.CartItemWithProduct
import com.hisuperaman.cartit.data.model.Cart
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest

class MainViewModel(application: Application, private val productRepository: ProductRepository, private val cartRepository: CartRepository, private val cartItemRepository: CartItemRepository, private val authViewModel: AuthViewModel) : AndroidViewModel(application) {
    val products = productRepository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val user = authViewModel.user

    val cartItems: StateFlow<List<CartItemWithProduct>> = user
        .flatMapLatest { currentUser ->
            // Fetch cart items based on the current user's ID
            cartItemRepository.getCartItemsWithProducts(currentUser?.id ?: 0)
                .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    val cart: StateFlow<Cart?> = user
        .flatMapLatest { currentUser ->
            cartRepository.getCart(currentUser?.id ?: 0)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun addToCart(productId: Int, quantity: Int?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val cartId = cart.value?.id ?: return@launch

            val existingCartItem = cartItemRepository.getCartItem(cartId = cartId, productId = productId)
            if(existingCartItem != null) {
                existingCartItem.quantity += quantity?:1
                cartItemRepository.updateCartItem(existingCartItem)
            }
            else {
                cartItemRepository.addCartItem(CartItem(cartId = cartId, productId = productId, quantity = quantity?:1))
            }

            val product = productRepository.getProductById(productId)
            cartRepository.updateCartIncrement(quantity?:1, (quantity?:1)*product.price, user.value?.id?:0)
            onSuccess()
        }
    }

    fun deleteFromCart(cartItemId: Int, productId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val cartId = cart.value?.id?:return@launch
            val existingCartItem = cartItemRepository.getCartItem(cartId = cartId, productId = productId)
            val product = productRepository.getProductById(productId)
            cartRepository.updateCartDecrement(existingCartItem?.quantity?:1, (existingCartItem?.quantity?:1)*product.price, user.value?.id?:0)
            cartItemRepository.deleteCartItem(cartItemId)
            onSuccess()
        }
    }

    fun incrementCartItemQuantity(cartItemId: Int, productId: Int) {
        viewModelScope.launch {
            cartItemRepository.incrementQuantity(cartItemId)
            val product = productRepository.getProductById(productId)
            cartRepository.updateCartIncrement(1, product.price, user.value?.id?:0)

        }
    }

    fun decrementCartItemQuantity(cartItemId: Int, productId: Int) {
        viewModelScope.launch {
            cartItemRepository.decrementQuantity(cartItemId)
            val product = productRepository.getProductById(productId)
            cartRepository.updateCartDecrement(1, product.price, user.value?.id?:0)
        }
    }
}