package co.tuister.domain.base

import java.net.ConnectException

abstract class UseCase<out Type, in Params> where Type : Any {

    abstract suspend fun run(params: Params): Either<Failure, Type>

    protected fun analyzeException(exception : Exception?) : Failure{
        // TODO Create cases
        return when (exception) {
           is  ConnectException -> Failure.NetworkConnection(exception)
           else -> Failure.UnknownException(exception)
        }
    }

}
