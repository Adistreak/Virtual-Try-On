package com.hisuperaman.cartit.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var name: String,
    var description: String,
    var price: Long,
    var image_res_id: Int,
)