package test.app.demooidc

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.Response
import retrofit2.http.POST

interface KeycloakApiService {
    @GET(Path.PATH_USER_INFO)
    suspend fun getUserInfo(@Header("Authorization") authHeader: String): Response<UserInfoResponse>

    @POST(Path.PATH_LOGOUT)
    suspend fun logout(
        @Header("Authorization") authHeader: String
    ): Response<Unit>
}

data class UserInfoResponse(
    val sub: String,
    val name: String?,
    val preferred_username: String?,
    val email: String?
)
