package co.tuister.uisers.modules.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.login.DownloadImageUseCase
import co.tuister.domain.usecases.login.UploadImageUseCase
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.modules.main.MainState
import co.tuister.uisers.modules.main.MainState.DownloadedImage
import co.tuister.uisers.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    val uploadImageUseCase: UploadImageUseCase,
    private val downloadImageUseCase: DownloadImageUseCase
) : BaseViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user get() = _user

    fun initialize(extrauser: User) {
        _user.value = extrauser
        downloadImage()
    }

    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                uploadImageUseCase.run(UploadImageUseCase.Params(uri, _user.value!!.email))
            }
        }
    }

    fun downloadImage() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val resultData =
                    downloadImageUseCase.run(DownloadImageUseCase.Params(user.value!!.email))
                resultData.fold({ failure ->

                }, {
                    setState(DownloadedImage(Result.Success(it)))
                })
            }
        }
    }
}
