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
    val expireAt:String
)

@Serializable
data class UserInfo(
    val firstName: String,
    val secondName: String?,
    val thirdName: String?,
    val lastName: String,
    val code: String?,
    val phone: String?,
    val email: String?,
    val logo: String?,
)


@Serializable
data class Store(
    val id:Int,
    val name: String,
    val logo :String,
    val cover :String,
    val typeId:Int,
    val likes:Int,
    val stars:Int,
    val reviews:Int,
    val latLng:String?,
    val subscriptions:Int,
    var storeConfig: StoreConfig?,
    val storeCurrencies:List<StoreCurrency>
)

@Serializable
data class StoreNestedSection(
    val id: Int,
    val storeSectionId: Int,
    val nestedSectionId: Int,
    val nestedSectionName: String
)

@Serializable
data class StoreSection(
    val id: Int,
    val sectionName: String,
    val sectionId: Int,
    val storeCategoryId: Int,
)

@Serializable
data class Home(
    val products: List<ProductView>,
    val stores: List<Store>,
    val ads :List<Ads>,
    var storeCategories: List<StoreCategory>,
    var storeSections:List<StoreSection>,
    var storeNestedSections:List<StoreNestedSection>,
    val storeTime:StoreTime,
    val videoData:List<VideoData>
)

@Serializable
data class VideoData(
    val url :String,
    var image: String,
    var isReels:Int,
)

@Serializable
data class Ads(
    val id: Int,
    val image: String,
    val productId:Int?,
)

@Serializable
data class StoreTime(
    val id: Int,
    val openAt: String,
    val closeAt:String,
    val day: Int,
    val isOpen : Int
)

@Serializable
data class CustomPrice(
    val id: Int,
    val storeProductId: Int,
    val price:String,
)

@Serializable
data class Currency(
    var id: Int,
    var name:String,
    var sign:String
)

@Serializable
data class ProductView(
    var id: Int,
    var name:String,
    val products:List<StoreProduct>
)


@Serializable
data class StoreCurrency(
    val currencyId: Int,
    val currencyName: String,
    val lessCartPrice: String,
    val deliveryPrice :String,
    val freeDeliveryPrice:Int
)



@Serializable
data class StoreConfig(
    val categories: List<Int>,
    val sections: List<Int>,
    val nestedSections: List<Int>,
    val products: List<Int>,
    val storeIdReference :Int
)



@Serializable
data class Language(val name:String,val code: String)

@Serializable
data class OrderAmount(
    val id: Int,
    val currencyName: String,
    val currencyId: Int,

    var amount: Double,
)






