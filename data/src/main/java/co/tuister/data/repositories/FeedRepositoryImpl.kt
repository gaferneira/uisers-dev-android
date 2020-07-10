package co.tuister.data.repositories

import co.tuister.data.dto.FeedCardDto
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.data.utils.FeedCollection
import co.tuister.data.utils.await
import co.tuister.data.utils.getSource
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.FeedCard
import co.tuister.domain.repositories.FeedRepository
import com.google.firebase.firestore.FirebaseFirestore

class FeedRepositoryImpl(
    private val db: FirebaseFirestore,
    private val connectivityUtil: ConnectivityUtil
) : BaseRepositoryImpl(), FeedRepository {

    private val feedbackCollection by lazy { FeedCollection(db) }

    override suspend fun getFeed(): Either<Failure, List<FeedCard>> {
        return eitherResult {
            val subjects = feedbackCollection.collection()
                .get(connectivityUtil.getSource())
                .await()!!
                .documents.map {
                    it.toObject(FeedCardDto::class.java)!!.toEntity()
                }

            subjects.sortedByDescending { it.date }
        }
    }
}
