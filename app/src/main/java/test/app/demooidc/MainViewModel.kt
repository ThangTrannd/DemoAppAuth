package test.app.demooidc

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

class MainViewModel(
    application: Application,
    private val userRepository: UserRepository
) : ViewModel() {

    private val config = AuthorizationServiceConfiguration(
        Uri.parse("${BuildConfig.BASE_URL}${Path.PATH_AUTH}"),
        Uri.parse("${BuildConfig.BASE_URL}${Path.PATH_TOKEN}")
    )
    private val authService = AuthorizationService(application)

    private val _userInfo = MutableStateFlow<UserInfoResponse?>(null)
    val userInfo: StateFlow<UserInfoResponse?> = _userInfo.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun fetchUserInfo(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = userRepository.getUserInfo(accessToken)
            _userInfo.value = userInfo
            _isLoggedIn.value = userInfo != null
        }
    }

    fun startLogin(activity: Activity) {
        val authRequest = AuthorizationRequest.Builder(
            config,
            "android-client",
            ResponseTypeValues.CODE,
            Uri.parse("${BuildConfig.APPLICATION_ID}:/oauth2redirect")
        )
            .setScope("openid email profile")
            .setPrompt("login")
            .build()
        val intent = authService.getAuthorizationRequestIntent(authRequest)
        activity.startActivityForResult(intent, AUTH_REQUEST_CODE)
    }

    fun handleAuthResult(response: AuthorizationResponse, context: Context) {
        val request = response.createTokenExchangeRequest()
        authService.performTokenRequest(request) { tokenResponse, exception ->
            if (tokenResponse != null) {
                val accessToken = tokenResponse.accessToken
                accessToken?.let {
                    SessionManager.saveAccessToken(context, it)
                    fetchUserInfo(it)
                }
            }
        }
    }

    fun logoutUser(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val accessToken = SessionManager.getAccessToken(context)
            if (userRepository.logoutUser(accessToken)) {
                SessionManager.clearUserSession(context)
                _userInfo.value = null
                _isLoggedIn.value = false
            }
        }
    }

    companion object {
        const val AUTH_REQUEST_CODE = 1001
    }
}
