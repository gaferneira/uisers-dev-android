package co.tuister.domain.usecases.login

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.repositories.UserRepository

class CampusUseCase(
    private val userRepository: UserRepository
) : NoParamsUseCase<List<String>>() {
    override suspend fun run(): Either<Failure, List<String>> {
        return try {
            Either.Right(userRepository.getCampus())
        } catch (e: Exception) {
            Either.Left(analyzeException(e))
        }
    }
}
