package co.tuister.uisers.utils.extensions

fun <R, T> Map<R, List<T>>.addToValueList(key: R, element: T): Map<R, List<T>> {
    val mutable = this.toMutableMap()
    mutable[key] = (this[key] ?: emptyList()) + element
    return mutable
}

inline fun <A : Any, B : Any> nullCheck2(item1: A?, item2: B?, f: (A, B) -> Unit) {
    if (item1 != null && item2 != null) {
        f(item1, item2)
    }
}
