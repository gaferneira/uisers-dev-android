package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    var id: String = "",
    var title: String = "",
    var description: String? = null,
    var dueDate: Long? = null,
    // DO, DOING, DONE
    var status: Int = 0,
    var reminder: Int? = null,
    var materialColor: Int = 0
) : Parcelable {
    companion object {
        const val STATUS_DO = 0
        const val STATUS_DOING = 1
        const val STATUS_DONE = 2
    }
}
