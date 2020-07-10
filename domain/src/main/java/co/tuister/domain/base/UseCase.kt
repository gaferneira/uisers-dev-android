package co.tuister.domain.base

interface UseCase<out Type, in Params> where Type : Any {
    suspend operator fun invoke(params: Params): Either<Failure, Type>
}
