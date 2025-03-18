package com.hisuperaman.cartit.data.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hisuperaman.cartit.data.database.CartItemRepository
import com.hisuperaman.cartit.data.database.CartRepository
import com.hisuperaman.cartit.data.database.ProductRepository

class MainAppViewModelFactory(private val application: Application, private val productRepository: ProductRepository, private val cartRepository: CartRepository, private val cartItemRepository: CartItemRepository, private val authViewModel: AuthViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, productRepository, cartRepository, cartItemRepository, authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
