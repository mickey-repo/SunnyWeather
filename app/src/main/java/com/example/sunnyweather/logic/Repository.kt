package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers

/**
 * 仓库类，统一管理数据请求，判断数据源来自本地还是网络
 */
object Repository {
    /**
     * 返回值类型LiveData<Result<List<Place>>>
     */
    fun searchPlaces(query:String)= liveData(Dispatchers.IO) {
        val result = try{
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if(placeResponse.status == "ok"){
                Result.success(placeResponse.places)
            }else{
                // 主动创建异常，手动传入异常对象
                // 根据上下文推导已知返回类型，所以不需要返回
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }catch (e: Exception){
            // 捕获异常，避免类型推断不准确，主动返回类型
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }
}