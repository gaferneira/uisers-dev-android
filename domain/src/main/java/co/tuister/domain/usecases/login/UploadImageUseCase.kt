package co.tuister.domain.usecases.login

import android.net.Uri
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.repositories.LoginRepository

class UploadImageUseCase(
    private val loginRepository: LoginRepository
) : UseCase<Boolean, UploadImageUseCase.Params> {
    override suspend fun invoke(params: Params): Either<Failure, Boolean> {
        return try {
            loginRepository.uploadImage(params.uri, params.email)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }

    data class Params(val uri: Uri, val email: String)
}
