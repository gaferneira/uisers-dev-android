package co.tuister.domain.entities

import java.io.Serializable

data class User(
    var name: String = "",
    var email: String = "",
    var career: String = "",
    var idCareer: String = "",
    var campus: String = "",
    var semester: String = "",
    var period: String = "",
    var fcmId: String = "",
    var code: String = "",
    val migration: Boolean = false
) : Serializable
