package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.FeedCard

interface FeedRepository {
    suspend fun getFeed(): Either<Failure, List<FeedCard>>
}
