package com.example.sunnyweather.logic.network

import com.example.sunnyweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 统一的网络数据源访问入口
 * 对所有网络请求的API进行封装
 */
object SunnyWeatherNetwork {
    private val placeService = ServiceCreator.create<PlaceService>()
    private val weatherService = ServiceCreator.create<WeatherService>()

    suspend fun getDailyWeather(lng:String, lat:String) = weatherService.getDailyWeather(lng, lat).await()
    suspend fun getRealtimeWeather(lng:String, lat:String) = weatherService.getRealtimeWeather(lng, lat).await()
    /**
     * 返回值类型PlaceResponse
     */
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T>{
                override fun onResponse(call: Call<T?>, response: Response<T?>) {
                    val body = response.body()
                    if(body!=null){
                        continuation.resume(body)
                    }else{
                        continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }

                override fun onFailure(call: Call<T?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })

        }
    }
}