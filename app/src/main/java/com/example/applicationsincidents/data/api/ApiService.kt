package com.example.applicationsincidents.data.api

import com.example.applicationsincidents.data.model.CreateUserRequest
import com.example.applicationsincidents.data.model.GenericResponse
import com.example.applicationsincidents.data.model.Incident
import com.example.applicationsincidents.data.model.IncidentResponse
import com.example.applicationsincidents.data.model.LoginRequest
import com.example.applicationsincidents.data.model.LoginResponse
import com.example.applicationsincidents.data.model.UpdateIncidentRequest
import com.example.applicationsincidents.data.model.User
import com.example.applicationsincidents.data.model.UserListResponse
import com.example.applicationsincidents.ui.admin.incidents.IncidentListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("incidents/list.php")
    suspend fun getIncidents(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null
    ): Response<IncidentResponse>

    @POST("auth/register.php")
    fun addUser(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): Call<Map<String, Any>>

    @POST("incidents/update.php")
    suspend fun updateIncident(
        @Header("Authorization") token: String,
        @Body body: UpdateIncidentRequest
    ): Response<ResponseBody>

    @Multipart
    @POST("incidents/create.php")
    fun addIncident(
        @Header("Authorization") token: String,
        @Part("type_incident") typeIncident: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<Map<String, Any>>

    @GET("incidents/report_pdf.php")
    suspend fun generateReport(
        @Header("Authorization") token: String,
        @Query("id_user") userId: String? = null,
        @Query("status") status: String? = null
    ): Response<ResponseBody>

    @GET("users/list.php")
    suspend fun getUsers(
        @Header("Authorization") token: String
    ): Response<UserListResponse>
}