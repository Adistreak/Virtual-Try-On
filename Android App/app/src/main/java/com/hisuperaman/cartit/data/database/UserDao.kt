package com.hisuperaman.cartit.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hisuperaman.cartit.data.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun addUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun findUserByEmailPassword(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun findUserByEmail(email: String): User?

    @Query("UPDATE users SET name = COALESCE(:name, name), password = COALESCE(:password, password), address = COALESCE(:address, address) where email = :email")
    suspend fun updateUserByEmail(name: String?, password: String?, email: String, address: String?)
}