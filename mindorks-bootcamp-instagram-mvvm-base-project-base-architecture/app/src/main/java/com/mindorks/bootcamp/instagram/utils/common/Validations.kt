package com.mindorks.bootcamp.instagram.utils.common

import com.mindorks.bootcamp.instagram.R
import java.util.regex.Pattern


object Validations {

    private val EMAIL_ADDRESS = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    private const val MIN_PASSWORD_LENGTH = 6

    fun validateLogin(email: String?, password: String?): List<Validation> =
        ArrayList<Validation>().apply {
            add(emailValidation(email))
            add(passwordValidation(password))
        }

    fun validateSignUp(email: String?, password: String?, fullName: String?): List<Validation> =
        ArrayList<Validation>().apply {
            add(emailValidation(email))
            add(passwordValidation(password))
            add(fullNameValidation(fullName))
        }

    private fun passwordValidation(password: String?): Validation =
        when {
            password.isNullOrBlank() -> Validation(
                Validation.Field.PASSWORD,
                Resource.error(R.string.password_field_empty)
            )
            password.length < MIN_PASSWORD_LENGTH -> Validation(
                Validation.Field.PASSWORD,
                Resource.error(R.string.password_field_small_length)
            )
            else -> Validation(Validation.Field.PASSWORD, Resource.success())
        }

    private fun emailValidation(email: String?): Validation =
        when {
            email.isNullOrBlank() -> Validation(Validation.Field.EMAIL, Resource.error(R.string.email_field_empty))
            !EMAIL_ADDRESS.matcher(email).matches() -> Validation(
                Validation.Field.EMAIL,
                Resource.error(R.string.email_field_invalid)
            )
            else -> Validation(Validation.Field.EMAIL, Resource.success())
        }

    private fun fullNameValidation(fullName: String?): Validation =
        when {
            fullName.isNullOrBlank() -> Validation(
                Validation.Field.FULL_NAME,
                Resource.error(R.string.full_name_field_empty)
            )
            else -> Validation(Validation.Field.FULL_NAME, Resource.success())
        }
}

data class Validation(val field: Field, val resource: Resource<Int>) {
    enum class Field {
        EMAIL,
        PASSWORD,
        FULL_NAME
    }

}
