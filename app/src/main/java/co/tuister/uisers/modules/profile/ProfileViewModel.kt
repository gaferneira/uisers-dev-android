package co.tuister.uisers.modules.profile

import androidx.lifecycle.MutableLiveData
import co.tuister.domain.entities.User
import co.tuister.uisers.common.BaseViewModel

class ProfileViewModel() : BaseViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user get() = _user

    fun initialize(extrauser: User) {
        _user.value = extrauser
    }
}
