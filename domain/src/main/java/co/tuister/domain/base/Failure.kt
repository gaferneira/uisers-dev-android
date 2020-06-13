package co.tuister.domain.base

import co.tuister.domain.base.Failure.FeatureFailure

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure(val error: Exception?, var param: String = "") {
    class NetworkConnection(error: Exception? = null) : Failure(error)
    class ServerError(error: Exception? = null) : Failure(error)
    class AuthWeakPasswordException(error: Exception? = null) : Failure(error)
    class AuthenticationError(error: Exception? = null) : Failure(error)
    class EmailNotVerifiedError(error: Exception? = null) : Failure(error)
    class FormError(error: Exception? = null) : Failure(error)
    class DataError(error: Exception? = null, param: String) : Failure(error, param)

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure(error: Exception?) : Failure(error)
}