package com.hisuperaman.cartit.data

import com.hisuperaman.cartit.R
import com.hisuperaman.cartit.data.model.Product

object DataSource {
    val sampleProducts = listOf(
        Product(name = "Red Shirt", description = "A casual red shirt", price = 499, image_res_id = R.drawable.red_shirt),
        Product(name = "Green Shirt", description = "A casual green shirt", price = 499, image_res_id = R.drawable.green_shirt),
        Product(name = "Blue Shirt", description = "A casual blue shirt", price = 499, image_res_id = R.drawable.blue_shirt),
        Product(name = "LVS Shirt", description = "Premium LVS shirt", price = 1299, image_res_id = R.drawable.lvs_shirt),
    )
}