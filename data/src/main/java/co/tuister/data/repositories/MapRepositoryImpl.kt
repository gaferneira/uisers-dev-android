package co.tuister.data.repositories

import co.tuister.data.dto.PlaceDto
import co.tuister.data.dto.SiteDto
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.BaseCollection
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Place
import co.tuister.domain.entities.Site
import co.tuister.domain.repositories.MapRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class MapRepositoryImpl(
    db: FirebaseFirestore,
    private val gson: Gson,
    private val connectivityUtil: ConnectivityUtil
) : BaseRepositoryImpl(), MapRepository {

    private val baseCollection by lazy { BaseCollection(db, connectivityUtil) }

    override suspend fun getPlaces(): Either<Failure, List<Place>> {
        return eitherResult {
            val document = baseCollection.getMapDocument()
            val field = document?.get(BaseCollection.FIELD_MAP_PLACES)
            val json = gson.toJson(field)
            gson.fromJson(json, Array<PlaceDto>::class.java).toList().map {
                it.toEntity()
            }
        }
    }

    override suspend fun getSites(): Either<Failure, List<Site>> {
        return eitherResult {
            val document = baseCollection.getMapDocument()
            val field = document?.get(BaseCollection.FIELD_MAP_SITES)
            val json = gson.toJson(field)
            gson.fromJson(json, Array<SiteDto>::class.java).toList().map {
                it.toEntity()
            }
        }
    }
}
