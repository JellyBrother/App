package com.example.myapp.base.net.constant;

import com.example.myapp.base.net.utils.ApiResponse;
import com.example.myapp.base.net.home.net.response.GetBannersRes;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public class PalmHouseApi {

    public interface JavaHome {
        /**
         * 获取首页banner图
         * /advertisement
         */
        @Headers("Content-Type:application/json")
        @GET("api/advertisement")
        Observable<ApiResponse<List<GetBannersRes>>> getBanners();
    }
}
