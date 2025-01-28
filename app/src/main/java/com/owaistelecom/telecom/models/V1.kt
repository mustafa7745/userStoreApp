package com.owaistelecom.telecom.models

import com.owaistelecom.telecom.models.StoreCategory
import kotlinx.serialization.Serializable


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
    val subscriptions:Int,
    var storeConfig: StoreConfig?
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
    val ads :List<Ads>,
    var storeCategories: List<StoreCategory>,
    var storeSections:List<StoreSection>,
    var storeNestedSections:List<StoreNestedSection>
)

@Serializable
data class Ads(
    val id: Int,
    val image: String,
    val pid:Int?,
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
data class StoreConfig(
    val categories: List<Int>,
    val sections: List<Int>,
    val nestedSections: List<Int>,
    val products: List<Int>,
    val storeIdReference :Int
)









