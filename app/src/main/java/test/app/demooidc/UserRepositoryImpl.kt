package test.app.demooidc

class UserRepositoryImpl(private val keycloakApi: KeycloakApiService) : UserRepository {

    override suspend fun getUserInfo(accessToken: String): UserInfoResponse? {
        return try {
            val response = keycloakApi.getUserInfo("Bearer $accessToken")
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun logoutUser(accessToken: String): Boolean {
        return try {
            val response = keycloakApi.logout("Bearer $accessToken")
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}