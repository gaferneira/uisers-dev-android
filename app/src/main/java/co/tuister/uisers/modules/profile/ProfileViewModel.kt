package co.tuister.uisers.modules.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tuister.domain.entities.Career
import co.tuister.domain.entities.User
import co.tuister.domain.usecases.login.CampusUseCase
import co.tuister.domain.usecases.login.CareersUseCase
import co.tuister.domain.usecases.login.DownloadImageUseCase
import co.tuister.domain.usecases.login.UploadImageUseCase
import co.tuister.domain.usecases.profile.ProfileUseCase
import co.tuister.uisers.common.BaseState
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.modules.main.MainViewModel.State.DownloadedImage
import co.tuister.uisers.modules.profile.ProfileViewModel.State.ValidateProfileUpdate
import co.tuister.uisers.utils.ProgressType.DOWNLOADING
import co.tuister.uisers.utils.Result
import co.tuister.uisers.utils.Result.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
  private val uploadImageUseCase: UploadImageUseCase,
  private val downloadImageUseCase: DownloadImageUseCase,
  private val careersUseCase: CareersUseCase,
  private val campusUseCase: CampusUseCase,
  private val profileUseCase: ProfileUseCase
) : BaseViewModel() {

    sealed class State<out T : Any>(result: Result<T>) : BaseState<T>(result) {
        class ValidateProfileUpdate(val result: Result<Boolean>) : State<Boolean>(result)
    }

    private val _user: MutableLiveData<User> = MutableLiveData()

    val user get() = _user
    val listCareers = mutableListOf<Career>()
    val listCampus = mutableListOf<String>()

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

    private fun downloadImage() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val resultData =
                    downloadImageUseCase.run(DownloadImageUseCase.Params(user.value!!.email))
                resultData.fold({ _ ->
                }, {
                    setState(DownloadedImage(Success(it)))
                })
            }
        }
    }

    fun getCareers(unit: () -> Unit) {
        if (listCareers.isEmpty()) {
            setState(ValidateProfileUpdate(InProgress(DOWNLOADING)))
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    val result =
                        careersUseCase.run()
                    result.fold({ fail ->
                        setState(ValidateProfileUpdate(Error(fail)))
                    }, { res ->
                        listCareers.addAll(res)
                        unit.invoke()
                    })
                }
            }
        } else {
            unit.invoke()
        }
    }

    fun updateProfile() {
        setState(ValidateProfileUpdate(InProgress()))
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val result = profileUseCase.run(ProfileUseCase.Params(_user.value!!))
                result.fold({ fail ->
                    setState(ValidateProfileUpdate(Error(fail)))
                }, { res ->
                    setState(ValidateProfileUpdate(Success(res)))
                })
            }
        }
    }

    fun getCampus(unit: () -> Unit) {
        if (listCampus.isEmpty()) {
            setState(ValidateProfileUpdate(InProgress(DOWNLOADING)))
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    val result =
                        campusUseCase.run()
                    result.fold({ fail ->
                        setState(ValidateProfileUpdate(Error(fail)))
                    }, { res ->
                        listCampus.addAll(res)
                        unit.invoke()
                    })
                }
            }
        } else {
            unit.invoke()
        }
    }
}
