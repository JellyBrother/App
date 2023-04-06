package com.example.myapp.home.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.example.myapp.base.bridge.home.net.model.HomeModel;
import com.example.myapp.base.widget.status.BaseListViewModel;

public class HomeFragmentVm extends BaseListViewModel<HomeModel> {
    public static final String HOMEFRAGMENT_RECOMMEND = "HomeFragment_recommend";
    private MutableLiveData<String> homeConfigEntityLiveData;

    public MutableLiveData<String> getHomeConfigEntityLiveData() {
        if (homeConfigEntityLiveData == null) {
            homeConfigEntityLiveData = new MutableLiveData<>();
        }
        return homeConfigEntityLiveData;
    }

    @Override
    public void startRequest() {
        super.startRequest();
        getMineCategorys();
    }

    private void getMineCategorys() {
//        Observable.zip(getModel().getBanners(), getModel().getBanners(), new BiFunction<ApiResponse<List<GetBannersRes>>, ApiResponse<List<GetBannersRes>>, Object>() {
//                    @Override
//                    public String apply(ApiResponse<List<GetBannersRes>> listApiResponse, ApiResponse<List<GetBannersRes>> listApiResponse2) throws Exception {
//                        return "";
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribeWith(new BaseObserver<String>() {
//                    @Override
//                    public void onNext(String o) {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        onRefreshFail();
//                    }
//                });
    }
}
