package co.tuister.data.utils

import timber.log.Timber
import java.util.*

inline fun <reified T> Any?.castToList(): List<T>? {
    return when (this) {
        is List<*> -> this.filterIsInstance<T>().takeIf { it.size == size }
        else -> null
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified K, reified V> Any?.castToHashMap(): HashMap<K, V>? {
    when (this) {
        is HashMap<*, *> -> {
            val verifyKeys = keys.filterIsInstance<K>().takeIf { it.size == keys.size }
            val verifyValues = values.filterIsInstance<V>().takeIf { it.size == values.size }
            if (verifyKeys != null && verifyValues != null) {
                return this as HashMap<K, V>
            }
        }
    }

    return null
}

fun Any.objectToMap(): Map<String, Any> {
    val map: MutableMap<String, Any> = HashMap()
    for (field in javaClass.declaredFields) {
        field.isAccessible = true
        try {
            field[this]?.let {
                map[field.name] = it
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
    return map
}
