package co.tuister.data.migration

import java.io.Serializable

class MigrationData : Serializable {
    var bloqueMateriaList: List<BloqueMateria> = listOf()
    var materiaEstudianteList: List<MateriaEstudiante> = listOf()
    var notaList: List<Nota> = listOf()
    var semestreEstudianteList: List<SemestreEstudiante> = listOf()
    var tareaList: List<Tarea> = listOf()
}

class Carrera : Serializable {
    var idCarrera = 0
    var nombreCarrera: String? = null
    var fkFacultad: Facultad? = null
}

class BloqueMateria : Serializable {
    var idBloqueMateria: String? = null
    var horaInicio: String? = null
    var horaFin: String? = null
    var lugar: String? = null
    var fkDia: Dia? = null
    var fkMateriaEstudiante: MateriaEstudiante? = null
    var fechaUpdate: String? = null
}

class Dia : Serializable {
    var idDia = 0
    var nombreDia: String? = null
}

class Estudiante : Serializable {
    var idEstudiante = 0
    var correo: String? = null
    var contrasena: String? = null
    var fkCarrera: Carrera? = null
    var idGSM: String? = null
    var ponderado = 0f
    var fechaUpdate: String? = null
}

class Facultad : Serializable {
    var idFacultad = 0
    var nombreFacultad: String? = null
}

class Materia : Serializable {
    var idMateria = 0
    var nombreMateria: String? = null
    var creditos = 0
    var requisitos: String? = null
}

class MateriaCarrera : Serializable {
    var idMateriaCarrera = 0
    var nivel = 0
    var fkMateria: Materia? = null
    var fkCarrera: Carrera? = null
}

class MateriaEstudiante : Serializable {
    var idMateriaEstudiante: String? = null
    var definitiva = 0f
    var profesor: String? = null
    var fechaUpdate: String? = null
    var fkSemestreEstudiante: SemestreEstudiante? = null
    var fkMateriaCarrera: MateriaCarrera? = null
}

class Nota : Serializable {
    var idNota: String? = null
    var descripcion: String? = null
    var valor = 0f
    var porcentaje = 0
    var fechaUpdate: String? = null
    var fkMateriaEstudiante: MateriaEstudiante? = null
    val total: Float
        get() = (porcentaje * valor / HUNDRED_PERCENT).toFloat()

    companion object {
        private const val HUNDRED_PERCENT = 100.0
    }
}

class Profesor : Serializable {
    var idProfesor = 0
    var nombreProfesor: String? = null
    var oficinaProfesor: String? = null
    var telefonoProfesor: String? = null
    var tipoProfesor: String? = null
    var calificacionProfesor: Double? = null
}

class Promo : Serializable {
    var descp: String? = null
    var direccion: String? = null
    var urlImg: String? = null
    var numero: String? = null
    var dateEnd: String? = null
    var isOwn = false
}

class Semestre : Serializable {
    var idSemestre = 0
    var ano = 0
    var periodo = 0

    override fun toString(): String {
        return "$ano-$periodo"
    }
}

class SemestreEstudiante : Serializable {
    var idSemestreEstudiante: String? = null
    var ponderado = 0f
    var fkEstudiante: Estudiante? = null
    var puntos = 0f
    var creditos = 0
    var fkSemestre: Semestre? = null
    var fechaUpdate: String? = null
}

class Tarea : Serializable {
    var idTarea: String? = null
    var titulo: String? = null
    var descripcion: String? = null
    var fecha: String? = null
    var horaInicio: String? = null
    var horaFin: String? = null
    var isConAlerta = false
    var isConFecha = false
    var horaAlerta: String? = null
    var fkEstudiante: Estudiante? = null
    var fechaUpdate: String? = null
}
