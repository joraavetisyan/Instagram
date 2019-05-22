package com.mindorks.bootcamp.instagram.ui.splash

import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.ui.dummy.DummyActivity
import com.mindorks.bootcamp.instagram.ui.login.LoginActivity
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable


class SplashViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val launchNextActivity: MutableLiveData<Class<*>> = MutableLiveData()

    override fun onCreate() {
        if (userRepository.getCurrentUser() != null) {
            launchNextActivity.postValue(DummyActivity::class.java)
        } else {
            launchNextActivity.postValue(LoginActivity::class.java)
        }
    }
}