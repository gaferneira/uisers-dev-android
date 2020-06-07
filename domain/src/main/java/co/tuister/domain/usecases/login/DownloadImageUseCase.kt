package co.tuister.domain.usecases.login

import android.net.Uri
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.repositories.UserRepository
import co.tuister.domain.usecases.login.DownloadImageUseCase.Params

class DownloadImageUseCase(
    private val userRepository: UserRepository
) : UseCase<Uri, Params>() {
    override suspend fun run(params: Params): Either<Failure, Uri> {
        return userRepository.downloadImage(params.email)
    }

    data class Params(val email: String)
}