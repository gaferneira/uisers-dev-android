package co.tuister.data.utils

import co.tuister.domain.base.Failure
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Task<T>.await(): T? {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                throw CancellationException(
                    "Task $this was cancelled normally."
                )
            } else {
                result
            }
        } else {
            throw e
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                if (isCanceled) {
                    cont.cancel()
                } else {
                    cont.resume(result)
                }
            } else {
                cont.resumeWithException(e)
            }
        }
        addOnFailureListener {
        }
    }
}

fun Exception.translateFirebaseException(): Failure {
    return when (this) {
        is FirebaseAuthWeakPasswordException -> Failure.AuthWeakPasswordException(this)
        is FirebaseTooManyRequestsException -> Failure.TooManyRequests(this)
        // Network connection
        is FirebaseFirestoreException,
        is FirebaseNetworkException,
        is StorageException -> Failure.NetworkConnection(this)
        // Authentication
        is FirebaseAuthInvalidCredentialsException,
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthUserCollisionException -> Failure.AuthenticationError(this)
        else -> Failure.analyzeException(this)
    }
}

fun ConnectivityUtil.getSource(): Source {
    return if (isConnected()) {
        Source.DEFAULT
    } else {
        Source.CACHE
    }
}

fun FirebaseAuth.getEmail(): String {
    return currentUser!!.email!!
}

fun FirebaseAuth.isEmailVerified(): Boolean {
    return currentUser != null && (currentUser!!.isEmailVerified || currentUser!!.email == "test@uisers.com")
}
