package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * 仓库类，统一管理数据请求，判断数据源来自本地还是网络
 */
object Repository {

    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    fun refreshWeather(lng:String, lat:String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng,lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng,lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if(realtimeResponse.status=="ok"&&dailyResponse.status=="ok"){
                val weather = Weather(realtimeResponse.result.realtime,dailyResponse.result.daily)
                Result.success(weather)
            }else{
                Result.failure(RuntimeException(
                    "realtime response status is ${realtimeResponse.status}"
                            +"daily response status is ${dailyResponse.status}"))
            }
        }
    }
    /**
     * 返回值类型LiveData<Result<List<Place>>>
     */
    fun searchPlaces(query:String)= fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if(placeResponse.status == "ok"){
            Result.success(placeResponse.places)
        }else{
            // 主动创建异常，手动传入异常对象
            // 根据上下文推导已知返回类型，所以不需要返回
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    private fun <T> fire(context: CoroutineContext,
                         block:suspend () -> Result<T>) =
        liveData<Result<T>>(context){
            val result = try{
                block()
            }catch (e: Exception){
                // 捕获异常，避免类型推断不准确，主动返回类型
                Result.failure<T>(e)
            }
            emit(result)
        }
}