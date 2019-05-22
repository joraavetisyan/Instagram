package com.mindorks.bootcamp.instagram.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.dummy.DummyActivity
import com.mindorks.bootcamp.instagram.utils.common.MyTextWatcher
import com.mindorks.bootcamp.instagram.utils.common.Status
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity<LoginViewModel>() {

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun provideLayoutId() = R.layout.activity_login

    override fun setupView(savedInstanceState: Bundle?) {
    }

    override fun setupObservers() {
        super.setupObservers()

        et_full_name.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onEmailChange(s.toString())
            }
        })

        et_password.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onPasswordChange(s.toString())
            }
        })

        viewModel.launchNextActivity.observe(this, Observer {
            startActivity(Intent(applicationContext, it))
        })

        viewModel.emailLiveData.observe(this, Observer {
            if (it != et_full_name.text.toString()) et_full_name.setText(it)
        })

        viewModel.passwordLiveData.observe(this, Observer {
            if (it != et_password.text.toString()) et_password.setText(it)
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

        viewModel.loadingLiveData.observe(this, Observer {
            pb_loading.visibility = if (it) View.VISIBLE else View.GONE
        })

        btn_login.setOnClickListener {
            viewModel.onLogin()
        }

        tv_sign_up.setOnClickListener {
            viewModel.onSignUpLinkClicked()
        }

    }

}