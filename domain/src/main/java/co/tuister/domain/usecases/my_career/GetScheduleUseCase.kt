package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.SubjectClass
import co.tuister.domain.repositories.SubjectRepository

class GetScheduleUseCase(
    private val repository: SubjectRepository
) : NoParamsUseCase<List<SubjectClass>>() {
    override suspend fun run(): Either<Failure, List<SubjectClass>> {
        return repository.getSchedule()
    }
}