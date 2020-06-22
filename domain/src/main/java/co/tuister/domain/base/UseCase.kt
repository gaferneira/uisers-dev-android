package co.tuister.domain.base

interface UseCase<out Type, in Params> where Type : Any {
    suspend fun run(params: Params): Either<Failure, Type>
}
