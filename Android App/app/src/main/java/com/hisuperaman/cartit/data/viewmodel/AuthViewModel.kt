package com.hisuperaman.cartit.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisuperaman.cartit.data.database.AppDatabase
import com.hisuperaman.cartit.data.database.CartRepository
import com.hisuperaman.cartit.data.database.UserRepository
import com.hisuperaman.cartit.data.datastore.UserPreferences
import com.hisuperaman.cartit.data.model.Cart
import com.hisuperaman.cartit.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(application: Application, private val userRepository: UserRepository, private val cartRepository: CartRepository) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    val isLoggedIn = UserPreferences.getUserSession(context).stateIn(
        viewModelScope, SharingStarted.Lazily, false
    )
    val userEmail = UserPreferences.getUserEmail(context).stateIn(
        viewModelScope, SharingStarted.Lazily, null
    )


    fun updateUserInfo(name: String?, password: String?, email: String, address: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.updateUser(name, password, email, address)
            getUserInfo(email)
            onSuccess()
        }
    }
    fun getUserInfo(email: String) {
        viewModelScope.launch {
            _user.value = userRepository.getUserByEmail(email)
        }
    }



    fun signUp(username: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val userId: Long = userRepository.registerUser(User(name = username, email = email, password = password))
                cartRepository.addCart(Cart(userId = userId.toInt(), totalItems = 0, totalPrice = 0))
                onSuccess()
            }
            catch(e: Exception) {
                onError("An account with provided email already exists!")
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val user = userRepository.loginUser(email, password)
            if (user != null) {
                UserPreferences.saveUserSession(context, email)
                onSuccess()
            } else {
                onError()
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            UserPreferences.clearSession(context)
            onSuccess()
        }
    }
}