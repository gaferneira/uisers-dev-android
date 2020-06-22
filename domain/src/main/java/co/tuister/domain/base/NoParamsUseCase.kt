package co.tuister.domain.base

interface NoParamsUseCase<out Type> where Type : Any {
    suspend fun run(): Either<Failure, Type>
}
