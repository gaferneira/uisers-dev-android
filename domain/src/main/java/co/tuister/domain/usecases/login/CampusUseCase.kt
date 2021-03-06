package co.tuister.domain.usecases.login

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.repositories.UserRepository

class CampusUseCase(
    private val userRepository: UserRepository
) : NoParamsUseCase<List<String>> {
    override suspend fun invoke(): Either<Failure, List<String>> {
        return try {
            userRepository.getCampus()
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
