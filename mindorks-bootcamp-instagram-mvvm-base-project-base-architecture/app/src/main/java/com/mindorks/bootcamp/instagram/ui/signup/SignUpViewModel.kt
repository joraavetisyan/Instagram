package com.mindorks.bootcamp.instagram.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.ui.dummy.DummyActivity
import com.mindorks.bootcamp.instagram.ui.login.LoginActivity
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.common.Status
import com.mindorks.bootcamp.instagram.utils.common.Validation
import com.mindorks.bootcamp.instagram.utils.common.Validations
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class SignUpViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val launchNextActivity: MutableLiveData<Class<*>> = MutableLiveData()

    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val emailLiveData: MutableLiveData<String> = MutableLiveData()
    val passwordLiveData: MutableLiveData<String> = MutableLiveData()
    val fullNameLiveData: MutableLiveData<String> = MutableLiveData()
    private val validationsLiveData: MutableLiveData<List<Validation>> = MutableLiveData()

    val emailValidationLiveData: LiveData<Resource<Int>> = filterValidations(Validation.Field.EMAIL)
    val passwordValidationLiveData: LiveData<Resource<Int>> = filterValidations(Validation.Field.PASSWORD)
    val fullNameValidationLiveData: LiveData<Resource<Int>> = filterValidations(Validation.Field.FULL_NAME)


    private fun filterValidations(field: Validation.Field) =
        Transformations.map(validationsLiveData) {
            it.find { validation -> validation.field == field }
                ?.run { return@run this.resource }
                ?: Resource.unknown()
        }

    fun onEmailChanged(email: String?) {
        emailLiveData.postValue(email)
    }

    fun onPasswordChanged(password: String?) {
        passwordLiveData.postValue(password)
    }

    fun onFullNameChanged(name: String?) {
        fullNameLiveData.postValue(name)
    }

    fun onLoginLinkClicked() = launchNextActivity.postValue(LoginActivity::class.java)

    fun onSignUp() {
        val email = emailLiveData.value
        val password = passwordLiveData.value
        val fullName = fullNameLiveData.value

        val validations = Validations.validateSignUp(email, password, fullName)
        validationsLiveData.postValue(validations)

        if (email != null && password != null && fullName != null && isFieldsValid(validations)) {
            loadingLiveData.postValue(true)
            compositeDisposable.add(
                userRepository.signUp(email, password, fullName)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(
                        {
                            userRepository.saveCurrentUser(it)
                            loadingLiveData.postValue(false)
                            launchNextActivity.postValue(DummyActivity::class.java)
                        },
                        {
                            handleNetworkError(it)
                            loadingLiveData.postValue(false)
                        })
            )
        }

    }

    private fun isFieldsValid(validations: List<Validation>) =
        validations.size == validations.filter { validation ->
            validation.resource.status == Status.SUCCESS
        }.size


    override fun onCreate() {
    }

}