package co.tuister.domain.usecases.login

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Career
import co.tuister.domain.repositories.UserRepository

class CareersUseCase(
    private val userRepository: UserRepository
) : NoParamsUseCase<List<Career>>() {
    override suspend fun run(): Either<Failure, List<Career>> {
        return try {
            Either.Right(userRepository.getCareers())
        } catch (e: Exception) {
            Either. Left(Failure.analyzeException(e))
        }
    }
}
