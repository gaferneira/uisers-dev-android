package co.tuister.domain.usecases

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Right
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.repositories.UserRepository

class FeedbackUseCase(
    private val repository: UserRepository
) : UseCase<Boolean, String>() {
    override suspend fun run(params: String): Either<Failure, Boolean> {
        return try {
            Right(repository.sendFeedback(params))
        } catch (e: Exception) {
            Either. Left(Failure.analyzeException(e))
        }
    }
}
