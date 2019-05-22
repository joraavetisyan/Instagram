package com.mindorks.bootcamp.instagram.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.utils.common.MyTextWatcher
import com.mindorks.bootcamp.instagram.utils.common.Status
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : BaseActivity<SignUpViewModel>() {

    override fun provideLayoutId() = R.layout.activity_signup

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.launchNextActivity.observe(this, Observer {
            startActivity(Intent(applicationContext, it))
        })

        viewModel.loadingLiveData.observe(this, Observer {
            if (it) pb_loading.visibility = View.VISIBLE else View.GONE
        })

        viewModel.emailLiveData.observe(this, Observer {
            if (it != et_email.text.toString()) et_email.setText(it)
        })

        viewModel.passwordLiveData.observe(this, Observer {
            if (it != et_password.text.toString()) et_password.setText(it)
        })

        viewModel.fullNameLiveData.observe(this, Observer {
            if (it != et_full_name.text.toString()) et_full_name.setText(it)
        })

        viewModel.emailValidationLiveData.observe(this, Observer {
            when (it.status) {
                Status.ERROR -> til_email.error = it.data?.run { getString(this) }
                else -> til_email.isErrorEnabled = false
            }
        })

        viewModel.passwordValidationLiveData.observe(this, Observer {
            when (it.status) {
                Status.ERROR -> til_password.error = it.data?.run { getString(this) }
                else -> til_password.isErrorEnabled = false
            }
        })

        viewModel.fullNameValidationLiveData.observe(this, Observer {
            when (it.status) {
                Status.ERROR -> til_full_name.error = it.data?.run { getString(this) }
                else -> til_full_name.isErrorEnabled = false
            }
        })

        et_email.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onEmailChanged(s.toString())
            }
        })

        et_password.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onPasswordChanged(s.toString())
            }
        })

        et_full_name.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onFullNameChanged(s.toString())
            }
        })

        btn_signup.setOnClickListener {
            viewModel.onSignUp()
        }

        tv_login.setOnClickListener {
            viewModel.onLoginLinkClicked()
        }

    }

}

