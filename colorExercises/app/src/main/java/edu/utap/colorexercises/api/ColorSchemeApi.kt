package edu.utap.colorexercises.api
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ColorSchemeApi {
    @GET("scheme{?mode,count}")
    suspend fun getScheme(@Query("mode") mode: String, @Query("count") count: Int) : ColorSchemeResponse
    data class ColorSchemeResponse(val results: ColorScheme)
}