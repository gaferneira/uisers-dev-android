package co.tuister.domain.usecases.profile

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.UserRepository

class ProfileUseCase(
    private val userRepository: UserRepository
) : UseCase<Boolean, ProfileUseCase.Params> {
    override suspend fun run(params: Params): Either<Failure, Boolean> {
        return userRepository.updateUser(params.user)
    }

    data class Params(val user: User)
}
