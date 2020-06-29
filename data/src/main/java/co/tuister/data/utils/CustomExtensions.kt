package co.tuister.data.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.ConnectException
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

fun Exception.translateFirebaseException(): java.lang.Exception {
    return when (this) {
        is FirebaseFirestoreException,
        is FirebaseNetworkException -> ConnectException(message)
        else -> this
    }
}

fun ConnectivityUtil.getSource(): Source {
    return if (isConnected()) {
        Source.SERVER
    } else {
        Source.CACHE
    }
}
