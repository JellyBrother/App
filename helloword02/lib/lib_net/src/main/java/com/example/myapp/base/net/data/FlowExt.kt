package com.example.myapp.base.net.data

import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.example.myapp.base.net.data.cache.NetworkCache
import com.example.myapp.base.net.data.error.NetErrorException
import com.example.myapp.base.net.data.error.NoNetException
import com.example.myapp.base.net.data.mapper.DefaultMapper
import com.example.myapp.base.net.data.mapper.Mapper
import com.example.myapp.base.net.data.remote.ApiResult
import com.example.myapp.base.net.data.remote.doFailure
import com.example.myapp.base.net.data.remote.doSuccess
import com.example.myapp.base.net.utils.NetworkResponse
import com.example.myapp.base.utils.GsonUtils
import com.example.myapp.base.utils.LogUtils
import com.example.myapp.base.utils.NetworkUtils
import kotlinx.coroutines.flow.*
import okio.IOException
import retrofit2.HttpException

inline fun <reified T> NetworkResponse<T>.asApiResult(isSuccessBlock: (() -> Boolean)): ApiResult<T> {
    return if (isSuccessBlock.invoke()) {
        asApiResultSuccess()
    } else {
        asApiResultFailure()
    }
}

inline fun <reified T> Flow<T>.asApiResultFlow(): Flow<ApiResult<T>> {
    return this.map {
        ApiResult.Success(it)
    }.catchNetworkError()
}

inline fun <reified T> NetworkResponse<T>.asApiResult(): ApiResult<T> = asApiResult {
    isSuccess()
}

inline fun <reified T> NetworkResponse<T>.realData(): T {
    return if (isSuccess()) {
        this.data ?: defaultData()
    } else {
        LogUtils.wTag("FlowExt", "http fail response : ${GsonUtils.toJson(this)}")
        throw this.error ?: NetworkResponse.ApiErrorException.newDefault(
            this.code,
            this.errMsg ?: "网络异常，请稍后重试"
        )
    }
}

inline fun <reified T> NetworkResponse<T>.asApiResultSuccess() =
    ApiResult.Success(data ?: T::class.java.newInstance())

fun <T> NetworkResponse<T>.asApiResultFailure(): ApiResult.Failure {
    return if (error == null) {
        LogUtils.wTag("FlowExt", "http error response : ${GsonUtils.toJson(this)}")
        ApiResult.Failure(
            NetworkResponse.ApiErrorException(
                code.toString(),
                errMsg
            )
        )
    } else {
        ApiResult.Failure(error)
    }
}

inline fun <reified T, R> Flow<ApiResult<T>>.transformApiResult(crossinline nestFlow: suspend (t: T) -> ApiResult<R>): Flow<ApiResult<R>> =
    transform {
        it.doSuccess { t ->
            emit(nestFlow.invoke(t))
        }
        it.doFailure { throwable ->
            emit(ApiResult.Failure(throwable))
        }
    }

inline fun <reified T, R> Flow<ApiResult<T>>.transformApiResultFlow(crossinline nestFlow: suspend (t: T) -> Flow<ApiResult<R>>): Flow<ApiResult<R>> =
    transform {
        it.doSuccess { t ->
            emitAll(nestFlow.invoke(t))
        }
        it.doFailure { throwable ->
            emit(ApiResult.Failure(throwable))
        }
    }

fun <T> Flow<ApiResult<T>>.catchNetworkError() =
    catch {
        emit(it.asApiResultFailure())
    }

fun Throwable.asApiResultFailure() =
    when (this) {
        is HttpException -> {
            LogUtils.wTag("FlowExt", "http exception : $this")
            val errorString = response()?.errorBody()?.string()
            try {
                val networkResponse = GsonUtils.getGson().fromJson<NetworkResponse<Nothing>>(
                    errorString,
                    object : TypeToken<NetworkResponse<Nothing>>() {}.type
                )
                networkResponse.asApiResultFailure()
            } catch (e: Exception) {
                ApiResult.Failure(NetErrorException())
            }
        }
        is IOException, is JsonSyntaxException -> {
            ApiResult.Failure(NetErrorException())
        }
        else -> {
            LogUtils.wTag("FlowExt", "other exception : $this")
            ApiResult.Failure(this)
        }
    }

object FlowHelper {
    inline fun <reified T> asFlow(crossinline requestBlock: suspend () -> NetworkResponse<T>) =
        flow {
            if (!NetworkUtils.isConnected()) {
                throw NoNetException()
            } else {
                val apiResponse = requestBlock.invoke()
                emit(apiResponse.realData())
            }
        }.cancellable()

    inline fun <reified T> asFlowResult(crossinline requestBlock: suspend () -> NetworkResponse<T>) =
        flow {
            // 假延时
            // delay(3000)
            val apiResponse = requestBlock.invoke()
            emit(apiResponse.asApiResult())
        }.cancellable()

    inline fun <reified RemoteData, reified LocalData, reified ModuleData> asFlowWithCache(
        remoteMapper: Mapper<RemoteData, LocalData> = DefaultMapper(),
        localMapper: Mapper<LocalData, ModuleData> = DefaultMapper(),
        cache: NetworkCache<LocalData>,
        crossinline requestBlock: suspend () -> NetworkResponse<RemoteData>
    ): Flow<ApiResult<ModuleData>> =
        flow {
            var localData = cache.get()
            if (localData == null) {
                val response = requestBlock.invoke()
                if (response.isSuccess()) {
                    localData = remoteMapper.map(response.data!!)
                    cache.put(localData!!)
                    emit(ApiResult.Success(localMapper.map(localData)))
                } else {
                    emit(
                        ApiResult.Failure(
                            Exception(
                                response.errMsg
                            )
                        )
                    )
                }
            } else {
                emit(ApiResult.Success(localMapper.map(localData)))
            }
        }.cancellable()
}