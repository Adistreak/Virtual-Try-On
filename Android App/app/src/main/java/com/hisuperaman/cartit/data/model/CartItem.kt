package com.hisuperaman.cartit.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = Cart::class,
            parentColumns = ["id"],
            childColumns = ["cartId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["cartId"]),
        Index(value = ["productId"])
    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var cartId: Int,
    var productId: Int,
    var quantity: Int,

    var createdAt: Long = System.currentTimeMillis() // Store timestamp
)