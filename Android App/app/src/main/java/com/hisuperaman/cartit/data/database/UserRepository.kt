package com.hisuperaman.cartit.data.database

import com.hisuperaman.cartit.data.model.User

class UserRepository(private val userDao: UserDao) {
    suspend fun registerUser(user: User): Long = userDao.addUser(user)
    suspend fun loginUser(email: String, password: String) = userDao.findUserByEmailPassword(email, password)

    suspend fun getUserByEmail(email: String) = userDao.findUserByEmail(email)

    suspend fun updateUser(name: String?, password: String?, email: String, address: String?) = userDao.updateUserByEmail(name, password, email, address)
}