package co.tuister.domain.entities

import java.io.Serializable

data class User(
    var name: String = "",
    var email: String = "",
    var career: String = "",
    var semester: String = "",
    var period: String = "",
    var fcmId: String = "",
    var code: String = ""
) : Serializable {
}