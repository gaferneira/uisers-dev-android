package co.tuister.data.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Place
import co.tuister.domain.entities.Site
import co.tuister.domain.repositories.MapRepository
import kotlinx.coroutines.delay

class MapRepositoryImpl : MapRepository {

    override suspend fun getPlaces(): List<Place> {
        delay(1000)
        return listOf(
            Place("PORTERIA CARRERA 27", "",Pair(7.138838,-73.120263), "1"),
            Place("AUDITORIO LUIS A. CALVO", "",Pair(7.139737,-73.120035), "1"),
            Place("ADMINISTRACIÓN", "",Pair(7.140586,-73.119798), "1"),
            Place("INSED", "",Pair(7.141104,-73.119536), "1"),
            Place("TEATRO AIRE LIBRE", "",Pair(7.141114,-73.120158), "1"),
            Place("ADMINISTRACION 2", "",Pair(7.141408,-73.119525), "1"),
            Place("BIENESTAR UNIVERSITARIO", "",Pair(7.141713,-73.119658), "1"),
            Place("LA PERLA", "",Pair(7.141800,-73.120048), "1"),
            Place("MANTEN. Y PLANTA FISICA", "",Pair(7.139613,-73.120863), "1"),
            Place("IM-INGENIERIA MECANICA", "",Pair(7.140208,-73.120724), "1"),
            Place("AULA MAX. MECANICA", "",Pair(7.140372,-73.120990), "1"),
            Place("BIBLIOTECA", "",Pair(7.140944,-73.120870), "1"),
            Place("PLANTA TELEFONICA", "",Pair(7.141282,-73.121099), "1"),
            Place("IL-LANGUAGE INSTITUE", "",Pair(7.141438,-73.120687), "1"),
            Place("II-INGENIERIA INDUSTRIAL", "",Pair(7.141854,-73.120732), "1"),
            Place("CARACT. DE MAT.", "",Pair(7.142749,-73.121748), "1"),
            Place("LL-LAB. LIVIANOS", "",Pair(7.140131,-73.121749), "1"),
            Place("CT-CAMILO TORRES", "",Pair(7.140394,-73.121532), "1"),
            Place("CENTIC", "",Pair(7.140725,-73.121569), "1"),
            Place("CAPRUIS Y FAVUIS", "",Pair(7.141256,-73.121548), "1"),
            Place("DISEÑO INDUSTRIAL", "",Pair(7.141445,-73.121230), "1"),
            Place("IE-ING. ELECTRICA", "",Pair(7.142028,-73.121294), "1"),
            Place("LAB. DE POSGRADO", "",Pair(7.140173,-73.122248), "1"),
            Place("IQ-ING. QUIMICA", "",Pair(7.140880,-73.122098), "1"),
            Place("AULA MAXIMA DE FISICA", "",Pair(7.141263,-73.121951), "1"),
            Place("CEIAM", "",Pair(7.141340,-73.122247), "1"),
            Place("LAB. ALTA TENSION", "",Pair(7.141831,-73.122170), "1"),
            Place("LAB. DE HIDRAULICA.", "",Pair(7.141824,-73.121799), "1"),
            Place("TALLERES DISEÑO", "",Pair(7.142066,-73.122042), "1"),
            Place("PLANTA DE ACEROS", "",Pair(7.140008,-73.122750), "1"),
            Place("JORGE BAUTISTA V.", "",Pair(7.140477,-73.122737), "1"),
            Place("LP-LABORATORIOS PESADOS", "",Pair(7.140939,-73.122763), "1"),
            Place("DC -DANIEL CASAS", "",Pair(7.141523,-73.122700), "1"),
            Place("RESIDENCIAS UNIV.", "",Pair(7.140184,-73.117658), "1"),
            Place("PORTERIA CRA 30", "",Pair(7.139281,-73.117316), "1"),
            Place("KIOSKO RESIDENCIAS", "",Pair(7.140275,-73.117683), "1"),
            Place("CH-CIENCIAS HUMANAS", "",Pair(7.139113,-73.121225), "1"),
            Place("JARDINERIA", "",Pair(7.139620,-73.122820), "1"),
            Place("CANCHAS DE TENIS", "",Pair(7.139708,-73.119078), "1"),
            Place("CANCHA 1. DE MARZO", "",Pair(7.140754,-73.118650), "1"),
            Place("CANCHA FUTBOL SUR", "",Pair(7.139708,-73.118420), "1"),
            Place("CANCHAS MULTIPLES", "",Pair(7.141131,-73.117628), "1"),
            Place("COLISEO", "",Pair(7.139854,-73.117831), "1"),
            Place("DIAMANTE DE SOFTBOL", "",Pair(7.140463,-73.116563), "1"),
            Place("CENIVAM", "",Pair(7.140289,-73.116035), "1"),
            Place("CAFETERIA", "",Pair(7.141255,-73.121072), "1"),
            Place("PORTERIA CRA 25", "",Pair(7.139872,-73.123105), "1"),
            Place("INVERNADERO", "",Pair(7.142697,-73.120929), "1"),
            Place("Hospital Universitario", "",Pair(7.128135, -73.113725), "2"),
            Place("Escuela de Enfermeria", "",Pair(7.128576, -73.114536), "2")
        )
    }

    override suspend fun getSites(): List<Site> {
        delay(1000)
        return listOf(
            Site("1","Campus Principal - Bucaramanga", "",Pair(7.138838,-73.120263)),
            Site("2","Facultad de Salud - Bucaramanga", "",Pair(7.128576, -73.114536))
        )
    }
}
