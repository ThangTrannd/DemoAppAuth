package test.app.demooidc

interface UserRepository {
    suspend fun getUserInfo(accessToken: String): UserInfoResponse?
    suspend fun logoutUser(accessToken: String): Boolean
}
