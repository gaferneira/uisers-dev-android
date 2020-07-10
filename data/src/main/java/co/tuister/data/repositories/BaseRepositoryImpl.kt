package co.tuister.data.repositories

import co.tuister.data.utils.translateFirebaseException
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure

open class BaseRepositoryImpl {

    suspend fun <T> eitherResult(operation: suspend() -> T): Either<Failure, T> {
        return try {
            Either.Right(operation.invoke())
        } catch (exception: Exception) {
            Either.Left(exception.translateFirebaseException())
        }
    }
}
