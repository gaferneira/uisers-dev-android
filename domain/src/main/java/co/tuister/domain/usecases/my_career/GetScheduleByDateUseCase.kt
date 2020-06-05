package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.SubjectClass
import co.tuister.domain.repositories.SubjectRepository
import java.util.*

class GetScheduleByDateUseCase(
    private val repository: SubjectRepository
) : UseCase<List<SubjectClass>, Date>() {

    override suspend fun run(params: Date): Either<Failure, List<SubjectClass>> {
        return repository.getScheduleByDate(params)
    }

}