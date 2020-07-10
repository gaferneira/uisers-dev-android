package co.tuister.domain.base

interface NoParamsUseCase<out Type> where Type : Any {
    suspend operator fun invoke(): Either<Failure, Type>
}
