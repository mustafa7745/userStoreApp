package com.owaistelecom.telecom.shared

import kotlinx.serialization.Serializable

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