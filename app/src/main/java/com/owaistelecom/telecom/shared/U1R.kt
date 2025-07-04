package com.owaistelecom.telecom.shared

import kotlinx.serialization.Serializable

@Serializable
class VarRemoteConfig(
    var BASE_URL:String,
    var BASE_IMAGE_URL:String,
    var SUB_FOLDER_PRODUCT:String,
    var SUB_FOLDER_STORE_COVERS:String,
    var SUB_FOLDER_STORE_LOGOS:String,
    var SUB_FOLDER_USERS_LOGOS:String,
    val VERSION:String = "v1/u" ,
)

@Serializable
data class RemoteConfigModel(
    val SUB_FOLDER_STORE_COVERS: String,
    val SUB_FOLDER_PRODUCT: String,
    val BASE_IMAGE_URL: String,
    val BASE_URL: String,
    val SUB_FOLDER_STORE_LOGOS: String,
    val SUB_FOLDER_USERS_LOGOS: String,
    val VERSION:String = "v1/u",
    val REMOTE_CONFIG_VERSION : Int
)