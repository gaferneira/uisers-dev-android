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
    var codigo: String = ""
)

fun UserDataDto.toEntity() = User(
    nombre,
    correo,
    carrera,
    idCarrera,
    sede,
    ingreso,
    periodo,
    code = codigo
)

fun User.toDTO() = UserDataDto(
    name,
    career,
    idCareer,
    campus,
    email,
    semester,
    period,
    code
)