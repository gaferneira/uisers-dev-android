package co.tuister.data.dto

import java.io.Serializable

class NoteDto(
    val percentage: String,
    val score: String,
    val description: String
) : Serializable {
    constructor() : this("", "", "")
}