package com.owaistelecom.telecom.models

import kotlinx.serialization.Serializable


@Serializable
data class StoreCategory(
    val id: Int,
    val categoryId: Int,
    val categoryName: String
)

@Serializable
data class StoreProduct(
    val product: Product,
    val storeNestedSectionId:Int,
    val options: List<ProductOption>,
)

@Serializable
data class ProductOption(
    val storeProductId: Int,
    val currency: Currency,
    val name: String,
    val price: String
)
@Serializable
data class ProductImage(
    val image: String
)
@Serializable
data class Option(
    val id : Int,
    val name: String
)
@Serializable
data class StoreProducts(val storeCategory: StoreCategory, val storeProducts: List<StoreProduct>)

@Serializable
data class MyProduct(
    val id:Int,
    val name: String,
    val description: String?,
    val images: List<ProductImage>
)

@Serializable
data class MyCategory(
    val id: Int,
    val name: String
)


@Serializable
data class Product(
    val productId: Int,
    val productName: String,
    val productDescription: String?,
    val images: List<ProductImage>
)


@Serializable
data class ErrorMessage(
    val message: String,
    val errors : List<String>,
    val code:Int
)

@Serializable
data class AccessToken(
    val token: String,
    val firstName: String,
    val lastName: String,
    val logo: String?,
    val expireAt:String
)
