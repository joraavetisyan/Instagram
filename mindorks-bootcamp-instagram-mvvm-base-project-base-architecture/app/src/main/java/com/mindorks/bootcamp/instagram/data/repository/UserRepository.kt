package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.local.db.DatabaseService
import com.mindorks.bootcamp.instagram.data.local.prefs.UserPreferences
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.request.LoginRequest
import com.mindorks.bootcamp.instagram.data.remote.request.SignUpRequest
import com.mindorks.bootcamp.instagram.data.remote.response.LoginResponse
import io.reactivex.Single
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val networkService: NetworkService,
    private val databaseService: DatabaseService,
    private val userPreferences: UserPreferences
) {

    fun saveCurrentUser(user: User) {
        userPreferences.setUserId(user.id)
        userPreferences.setUserName(user.name)
        userPreferences.setUserEmail(user.email)
        userPreferences.setAccessToken(user.accessToken)
    }

    fun removeCurrentUser() {
        userPreferences.removeUserId()
        userPreferences.removeUserName()
        userPreferences.removeUserEmail()
        userPreferences.removeAccessToken()
    }

    fun getCurrentUser(): User? {

        val userId = userPreferences.getUserId()
        val userName = userPreferences.getUserName()
        val userEmail = userPreferences.getUserEmail()
        val accessToken = userPreferences.getAccessToken()

        return if (userId !== null && userName != null && userEmail != null && accessToken != null)
            User(userId, userName, userEmail, accessToken)
        else
            null
    }

    fun login(email: String, password: String): Single<User> =
        networkService.doLoginCall(LoginRequest(email, password)).map {
            User(
                it.userId,
                it.userName,
                it.userEmail,
                it.accessToken
            )
        }

    fun signUp(email: String, password: String, name: String) =
        networkService.doSignUpCall(SignUpRequest(email, password, name)).map {
            User(
                it.userId,
                it.userName,
                it.userEmail,
                it.accessToken
            )
        }

}