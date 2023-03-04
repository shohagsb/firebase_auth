package me.shohag.firebaseauthentication.authentication.view.main
import androidx.lifecycle.map

import androidx.lifecycle.ViewModel
import me.shohag.firebaseauthentication.authentication.firebase_utils.FirebaseUserLiveData

class MainViewModel : ViewModel() {

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}

enum class AuthenticationState {
    AUTHENTICATED, UNAUTHENTICATED
}