package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.base.getOrElse
import co.tuister.domain.entities.CareerSubject
import co.tuister.domain.repositories.SubjectRepository
import co.tuister.domain.repositories.UserRepository

class GetAllSubjectsUseCase(
    private val repository: SubjectRepository,
    private val userRepository: UserRepository
) : NoParamsUseCase<List<CareerSubject>> {

    override suspend fun invoke(): Either<Failure, List<CareerSubject>> {
        return try {
            val list = repository.getAll().getOrElse(listOf())
            val userCareer = userCareer()
            val filterList = list.groupBy { it.id }.map { map ->
                val item = if (map.component2().size == 1) {
                    map.component2().first()
                } else {
                    map.component2().singleOrNull() { it.career == userCareer } ?: map.component2().first()
                }
                item
            }
            Either.Right(filterList)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }

    private suspend fun userCareer(): String? {
        return when (val result = userRepository.getUser()) {
            is Either.Right -> result.value.career
            else -> null
        }
    }
}
