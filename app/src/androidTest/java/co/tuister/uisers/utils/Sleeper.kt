package co.tuister.uisers.utils

import java.time.Duration

/**
 * Abstraction around [Thread.sleep] to permit better testability.
 */
class Sleeper {

    @Throws(InterruptedException::class)
    fun sleep(duration: Duration) {
        Thread.sleep(duration.toMillis())
    }
}
