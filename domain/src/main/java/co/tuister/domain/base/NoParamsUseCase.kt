package co.tuister.domain.base

abstract class NoParamsUseCase<out T : Any> : UseCase<T, Nothing?>() {

    abstract suspend fun run(): Either<Failure, T>
    override suspend fun run(params: Nothing?): Either<Failure, T> = run()

}