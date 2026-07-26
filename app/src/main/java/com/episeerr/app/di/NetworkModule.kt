package com.episeerr.app.di

import com.episeerr.app.data.PreferencesRepository
import com.episeerr.app.data.SessionManager
import com.episeerr.app.data.api.EpiseerrApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

private const val PLACEHOLDER_BASE_URL = "http://episeerr.placeholder/"

/** In-memory cookie store so the Flask session cookie survives across requests for the app's lifetime. */
private class InMemoryCookieJar : CookieJar {
    private val store = mutableMapOf<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isNotEmpty()) store[url.host] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> = store[url.host] ?: emptyList()
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        preferencesRepository: PreferencesRepository,
        sessionManager: SessionManager
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .cookieJar(InMemoryCookieJar())
            .addInterceptor { chain ->
                val original = chain.request()

                // The server URL is user-configurable at runtime (stored in DataStore), so
                // Retrofit is built with a placeholder base URL and this interceptor swaps
                // in the real host/scheme/port on every request.
                val configuredUrl = runBlocking { preferencesRepository.getServerUrl() }
                val configuredHttpUrl = configuredUrl.toHttpUrlOrNull()

                val newUrl = if (configuredHttpUrl != null) {
                    original.url.newBuilder()
                        .scheme(configuredHttpUrl.scheme)
                        .host(configuredHttpUrl.host)
                        .port(configuredHttpUrl.port)
                        .build()
                } else {
                    original.url
                }

                val request = original.newBuilder()
                    .url(newUrl)
                    // Ensures a REQUIRE_AUTH=true server answers with a 401 JSON body instead
                    // of a 302 HTML redirect to /login (see episeerr.py's auth gate).
                    .header("X-Requested-With", "XMLHttpRequest")
                    .build()

                val response = chain.proceed(request)
                if (response.code == 401) {
                    sessionManager.markUnauthorized()
                } else if (response.isSuccessful) {
                    sessionManager.markAuthenticated()
                }
                response
            }
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(PLACEHOLDER_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideEpiseerrApi(retrofit: Retrofit): EpiseerrApi = retrofit.create(EpiseerrApi::class.java)
}
