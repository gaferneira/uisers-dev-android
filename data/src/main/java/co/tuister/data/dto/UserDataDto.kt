package co.tuister.data.dto

import co.tuister.domain.entities.User

data class UserDataDto(
    var nombre: String = "",
    var carrera: String = "",
    var idCarrera: String = "",
    var sede: String = "",
    var correo: String = "",
    var ingreso: String = "",
    var periodo: String = "",
    var codigo: String = "",
    var fcmId: String = ""
)

fun UserDataDto.toEntity() = User(
    nombre,
    correo,
    carrera,
    idCarrera,
    sede,
    ingreso,
    periodo,
    fcmId,
    codigo
)

fun User.toDTO() = UserDataDto(
    name,
    career,
    idCareer,
    campus,
    email.toLowerCase(),
    semester,
    period,
    code
)
