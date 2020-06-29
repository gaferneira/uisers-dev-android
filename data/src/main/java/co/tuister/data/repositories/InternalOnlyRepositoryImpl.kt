package co.tuister.data.repositories

import android.content.Context
import co.tuister.data.dto.CareerDto
import co.tuister.data.dto.CareerSubjectDto
import co.tuister.data.dto.EventDto
import co.tuister.data.dto.PlaceDto
import co.tuister.data.dto.SiteDto
import co.tuister.data.dto.UserDataDto
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.BaseCollection
import co.tuister.data.utils.BaseCollection.Companion.FIELD_CALENDAR
import co.tuister.data.utils.BaseCollection.Companion.FIELD_CAREERS
import co.tuister.data.utils.BaseCollection.Companion.FIELD_MAP_PLACES
import co.tuister.data.utils.BaseCollection.Companion.FIELD_MAP_SITES
import co.tuister.data.utils.BaseCollection.Companion.FIELD_SUBJECTS
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.data.utils.UsersCollection
import co.tuister.data.utils.await
import co.tuister.data.utils.objectToMap
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.InternalOnlyRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class InternalOnlyRepositoryImpl(
    private val context: Context,
    private val gson: Gson,
    private val db: FirebaseFirestore,
    private val connectivityUtil: ConnectivityUtil
) : InternalOnlyRepository {

    private val baseCollection by lazy { BaseCollection(db, connectivityUtil) }
    private val usersCollection by lazy { UsersCollection(db, connectivityUtil) }

    override suspend fun loadDataCareers(): Either<Failure, Boolean> {
        return try {
            context.assets.open("careers.json").bufferedReader().use {
                val text = it.readText()

                val list = gson.fromJson(text, Array<CareerDto>::class.java).toList()
                    .map { subject -> subject.objectToMap() }

                baseCollection.document(BaseCollection.DOCUMENT)
                    .update(mapOf(Pair(FIELD_CAREERS, list)))
                    .await()
            }
            Either.Right(true)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }

    override suspend fun loadDataSubjects(): Either<Failure, Boolean> {
        return try {
            context.assets.open("subjects.json").bufferedReader().use {
                val text = it.readText()

                val list = gson.fromJson(text, Array<CareerSubjectDto>::class.java).toList()
                    .map { subject -> subject.objectToMap() }

                baseCollection.document(BaseCollection.DOCUMENT)
                    .update(mapOf(Pair(FIELD_SUBJECTS, list)))
                    .await()
            }
            Either.Right(true)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }

    override
    suspend fun getAllUserData(): Either<Failure, List<User>> {
        return try {
            val data = usersCollection.getAllUserData()!!.map {
                it.toObject(UserDataDto::class.java)!!.toEntity()
            }
            Either.Right(data)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }

    override suspend fun loadMapData(): Either<Failure, Boolean> {
        return try {

            context.assets.open("map_places.json").bufferedReader().use {
                val text = it.readText()

                val list = gson.fromJson(text, Array<PlaceDto>::class.java).toList()
                    .map { place -> place.objectToMap() }

                baseCollection.getMapDocument()!!
                    .reference.update(mapOf(Pair(FIELD_MAP_PLACES, list)))
                    .await()
            }

            context.assets.open("map_sites.json").bufferedReader().use {
                val text = it.readText()

                val list = gson.fromJson(text, Array<SiteDto>::class.java).toList()
                    .map { site -> site.objectToMap() }

                baseCollection.getMapDocument()!!
                    .reference
                    .update(mapOf(Pair(FIELD_MAP_SITES, list)))
                    .await()
            }
            Either.Right(true)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }

    override suspend fun loadDataCalendar(): Either<Failure, Boolean> {
        return try {
            context.assets.open("calendar.json").bufferedReader().use {
                val text = it.readText()

                val list = gson.fromJson(text, Array<EventDto>::class.java).toList()
                    .map { dto ->
                        dto.objectToMap()
                    }

                baseCollection.document(BaseCollection.DOCUMENT_CALENDAR)
                    .update(mapOf(Pair(FIELD_CALENDAR, list)))
                    .await()
            }
            Either.Right(true)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
