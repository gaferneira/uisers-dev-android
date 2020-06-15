package co.tuister.domain.usecases

import android.util.Log
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.InternalOnlyRepository
import kotlin.system.measureTimeMillis

class DataUserUseCase(
    private val internalOnlyRepository: InternalOnlyRepository
) : NoParamsUseCase<List<User>>() {
    override suspend fun run(): Either<Failure, List<User>> {
        var result: Either<Failure, List<User>> = Either.Left(Failure.ServerError())
        val checkTime = measureTimeMillis {
            result = internalOnlyRepository.getAllUserData()
        }
        Log.d("checkTime", checkTime.toString())
        return result
    }
}