package com.hisuperaman.cartit.data.database

import com.hisuperaman.cartit.data.model.Product

class ProductRepository(private val productDao: ProductDao) {
    suspend fun addProduct(product: Product) = productDao.addProduct(product)
    suspend fun addProducts(products: List<Product>) = productDao.addProducts(products)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
    fun getAllProducts() = productDao.getAllProducts()

    suspend fun getProductById(productId: Int) = productDao.getProductById(productId)

}