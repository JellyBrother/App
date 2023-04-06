package com.example.myapp.base.bridge.home.net.model;

import com.example.myapp.base.net.constant.PalmHouseApi;
import com.example.myapp.base.net.home.net.response.GetBannersRes;
import com.example.myapp.base.net.utils.ApiResponse;
import com.example.myapp.base.net.utils.RetrofitCreateHelper;
import com.example.myapp.base.rxjava.RxjavaUtil;
import com.example.myapp.base.viewmodel.BaseModel;

import java.util.List;

import io.reactivex.Observable;

public class HomeModel extends BaseModel {
    /**
     * 获取首页banner图
     */
    public Observable<ApiResponse<List<GetBannersRes>>> getBanners() {
        return RxjavaUtil.setObservable2(RetrofitCreateHelper.getInstance().createJava(PalmHouseApi.JavaHome.class).getBanners());
    }
}
