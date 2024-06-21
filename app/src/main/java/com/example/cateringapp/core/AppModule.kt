package com.example.cateringapp.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.cateringapp.remote.datasources.AuthRemoteDataSource
import com.example.cateringapp.remote.datasources.BookingRemoteDataSource
import com.example.cateringapp.remote.services.AuthService
import com.example.cateringapp.remote.datasources.BusinessRemoteDataSource
import com.example.cateringapp.remote.datasources.MenuRemoteDataSource
import com.example.cateringapp.remote.datasources.NewsRemoteDataSource
import com.example.cateringapp.remote.datasources.OrderRemoteDataSource
import com.example.cateringapp.remote.datasources.UserRemoteDataSource
import com.example.cateringapp.remote.services.BookingService
import com.example.cateringapp.remote.services.BusinessService
import com.example.cateringapp.remote.services.MenuService
import com.example.cateringapp.remote.services.NewsService
import com.example.cateringapp.remote.services.OrderService
import com.example.cateringapp.remote.services.UserService
import com.example.cateringapp.remote.utils.CateringAccessTokenInterceptor
import com.example.cateringapp.utils.AppConsts
import com.skydoves.retrofit.adapters.result.ResultCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun getDataStoreRepo(dataStore: DataStore<Preferences>): UserPreferencesRepository =
        UserPreferencesRepository(dataStore)

    @Provides
    fun getAuthRemote(authService: AuthService) = AuthRemoteDataSource(authService)

    @Provides
    fun getBisRemote(businessService: BusinessService) = BusinessRemoteDataSource(businessService)

    @Provides
    fun getUserRemote(userService: UserService) = UserRemoteDataSource(userService)

    @Provides
    fun getMenuRemote(menuService: MenuService) = MenuRemoteDataSource(menuService)


    @Provides
    fun getNewsRemote(newsService: NewsService) = NewsRemoteDataSource(newsService)

    @Provides
    fun getBookingRemote(bookingService: BookingService) = BookingRemoteDataSource(bookingService)

    @Provides
    fun getOrderRemote(orderService: OrderService) = OrderRemoteDataSource(orderService)

    @Provides
    fun getAuthService(retrofit: Retrofit) = retrofit.create(AuthService::class.java)

    @Provides
    fun getBisService(retrofit: Retrofit) = retrofit.create(BusinessService::class.java)

    @Provides
    fun getUserService(retrofit: Retrofit) = retrofit.create(UserService::class.java)

    @Provides
    fun getMenuService(retrofit: Retrofit) = retrofit.create(MenuService::class.java)

    @Provides
    fun getBookingService(retrofit: Retrofit) = retrofit.create(BookingService::class.java)

    @Provides
    fun getOrderService(retrofit: Retrofit) = retrofit.create(OrderService::class.java)

    @Provides
    fun getNewsService(retrofit: Retrofit) = retrofit.create(NewsService::class.java)

    @Provides
    fun getDataStore(@ApplicationContext appContext: Context) = appContext.dataStore

    @Provides
    @Named("token")
    fun providesToken(userPrefs: UserPreferencesRepository): String? {
        return runBlocking { userPrefs.getToken()}
    }

    @Provides
    fun providesTokenFlow(userPrefs: UserPreferencesRepository): Flow<String?> {
        return userPrefs.userTokenFlow
    }

    @Provides
    @Named("url")
    fun providesBaseUrl(): String = AppConsts.baseUrl
    // 192.168.196.5 192.168.0.106

    @Provides
    fun provideRetrofit(
        accessToken: Flow<String?>,
        @Named("url") baseUrl: String,
    ): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(CateringAccessTokenInterceptor(accessToken))
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                Json.asConverterFactory("application/json; charset=UTF8".toMediaType())
            )
            .addCallAdapterFactory(ResultCallAdapterFactory.create())
            .client(okHttp)
            .build()
        return retrofit
    }


}