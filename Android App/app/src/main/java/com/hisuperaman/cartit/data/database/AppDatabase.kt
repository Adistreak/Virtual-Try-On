package com.hisuperaman.cartit.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hisuperaman.cartit.data.DataSource
import com.hisuperaman.cartit.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.hisuperaman.cartit.data.database.ProductDao
import com.hisuperaman.cartit.data.model.Cart
import com.hisuperaman.cartit.data.model.CartItem
import com.hisuperaman.cartit.data.model.Product

@Database(
    entities = [
        (User::class),
        (Product::class),
        (Cart::class),
        (CartItem::class)
    ],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun cartItemDao(): CartItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            db.execSQL("PRAGMA foreign_keys=ON;") // Enable foreign keys
                        }
                    })
                    .build()
                INSTANCE = instance

                // Ensure data is inserted after the database instance is created
                CoroutineScope(Dispatchers.IO).launch {
                    if (instance.productDao().getProductCount() == 0) { // Check if products exist
                        instance.productDao().addProducts(DataSource.sampleProducts)
                    }
                }

                instance
            }
        }
    }
}