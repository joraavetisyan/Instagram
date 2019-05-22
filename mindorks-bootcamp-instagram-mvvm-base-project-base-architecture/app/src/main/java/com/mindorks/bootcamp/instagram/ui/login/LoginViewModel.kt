package com.mindorks.bootcamp.instagram.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.ui.dummy.DummyActivity
import com.mindorks.bootcamp.instagram.ui.signup.SignUpActivity
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.common.Status
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.mindorks.bootcamp.instagram.utils.common.Validation
import com.mindorks.bootcamp.instagram.utils.common.Validations
import io.reactivex.disposables.CompositeDisposable

class LoginViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val launchNextActivity: MutableLiveData<Class<*>> = MutableLiveData()

    val emailLiveData: MutableLiveData<String> = MutableLiveData()
    val passwordLiveData: MutableLiveData<String> = MutableLiveData()
    private val validationsLiveData = MutableLiveData<List<Validation>>()

    val emailValidationLiveData: LiveData<Resource<Int>> = filterValidations(Validation.Field.EMAIL)
    val passwordValidationLiveData: LiveData<Resource<Int>> = filterValidations(Validation.Field.PASSWORD)

    fun onEmailChange(email: String?) {
        emailLiveData.postValue(email)
    }

    fun onPasswordChange(password: String?) {
        passwordLiveData.postValue(password)
    }

    fun onSignUpLinkClicked() = launchNextActivity.postValue(SignUpActivity::class.java)

    private fun filterValidations(field: Validation.Field) =
        Transformations.map(validationsLiveData) {
            it.find { validation -> validation.field == field }
                ?.run { return@run this.resource }
                ?: Resource.unknown()
        }

    val loadingLiveData = MutableLiveData<Boolean>()

    override fun onCreate() {
    }

    fun onLogin() {
        val email = emailLiveData.value
        val password = passwordLiveData.value
        var validations = Validations.validateLogin(email, password)
        validationsLiveData.postValue(validations)
        if (isFieldsValid(validations) && checkInternetConnectionWithMessage() && email != null && password != null) {
            loadingLiveData.postValue(true)
            compositeDisposable.add(
                userRepository.login(email, password)
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
        validations.size == validations.filter { it.resource.status == Status.SUCCESS }.size

}