package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Semester
import co.tuister.domain.repositories.SemesterRepository

class GetAllSemestersUseCase(
    private val repository: SemesterRepository
) : NoParamsUseCase<List<Semester>>() {

    override suspend fun run(): Either<Failure, List<Semester>> {
        return repository.getAll()
    }

}