package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.CareerSubject
import co.tuister.domain.repositories.SubjectRepository
import co.tuister.domain.repositories.UserRepository

class GetAllSubjectsUseCase(
    private val repository: SubjectRepository,
    private val userRepository: UserRepository
) : NoParamsUseCase<List<CareerSubject>>() {

    override suspend fun run(): Either<Failure, List<CareerSubject>> {

        when (val result = repository.getAll()) {
            is Either.Right -> {
                val list = result.value
                val userCareer = userCareer()
                val filterList = list.groupBy {it.id}.map { map ->
                    val item = if (map.component2().size == 1) {
                        map.component2().first()
                    } else {
                        map.component2().singleOrNull() { it.career == userCareer } ?: map.component2().first()
                    }
                    item
                }
                return Either.Right(filterList)
            } else -> {
              return result
            }
        }
    }

    private suspend fun userCareer() : String? {
        return when (val result = userRepository.getUser()){
            is Either.Right -> result.value.career
            else -> null
        }
    }


}