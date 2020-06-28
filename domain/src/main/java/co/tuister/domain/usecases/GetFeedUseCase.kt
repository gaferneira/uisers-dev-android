package co.tuister.domain.usecases

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.FeedCard
import co.tuister.domain.repositories.FeedRepository

class GetFeedUseCase(
    private val repository: FeedRepository
) : NoParamsUseCase<List<FeedCard>> {
    override suspend fun run(): Either<Failure, List<FeedCard>> {
        return try {
            val result = repository.getFeed()
            Either.Right(result)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
